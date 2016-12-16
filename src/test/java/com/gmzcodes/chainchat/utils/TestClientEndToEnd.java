package com.gmzcodes.chainchat.utils;

import static com.gmzcodes.chainchat.constants.ServerConfigValues.*;
import static com.gmzcodes.chainchat.utils.JsonAssert.assertJsonEquals;
import static io.vertx.core.http.HttpHeaders.COOKIE;
import static io.vertx.core.http.HttpHeaders.SET_COOKIE;

import java.util.List;

import com.gmzcodes.chainchat.constants.ExpectedValues;

import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;

/**
 * Created by danigamez on 08/12/2016.
 */
public class TestClientEndToEnd extends TestClient {
    private int PORT;

    public TestClientEndToEnd(int PORT) {
        this.PORT = PORT;
    }

    // TODO: Implement and test logout

    // MAIN FUNCTIONALITY:

    public void login(TestContext context, HttpClient client, String username, String password, Handler<JsonObject> handler) {
        String clientId = normalizeClientId(username);

        final String currentIdentifier;

        if (COOKIES.containsKey(clientId)) {
            int instanceNumber = COOKIES.size();

            while (!COOKIES.containsKey(username + "@" + instanceNumber) && --instanceNumber > 0);

            currentIdentifier = username + "@" + (instanceNumber + 1);
        } else {
            currentIdentifier = username + "@1";
        }

        // Login handler:

        HttpClientRequest req = client.post(PORT, HOSTNAME, PATHNAME_AUTHENTICATION, response -> {
            context.assertEquals(200, response.statusCode());

            // COOKIE / SESSION CHECKS:

            MultiMap headers = response.headers();

            context.assertNotNull(headers);
            context.assertTrue(headers.contains(SET_COOKIE));

            String setCookie = response.headers().get(SET_COOKIE);

            context.assertNotNull(setCookie);
            context.assertFalse(setCookie.isEmpty());

            COOKIES.put(currentIdentifier, setCookie); // Keep session cookie for later

            response.bodyHandler(body -> {
                // TOKEN CHECKS:

                JsonObject jsonResponse = body.toJsonObject();

                context.assertTrue(jsonResponse.containsKey("token"));

                String setToken = jsonResponse.getString("token");

                context.assertNotNull(setToken);
                context.assertFalse(setToken.isEmpty());

                TOKENS.put(currentIdentifier, setToken);

                System.out.println("┌───────────────────────────────");
                System.out.println("│ CLIENT ID: " + currentIdentifier);
                System.out.println("├───────────────────────────────");
                System.out.println("│      USER: " + username);
                System.out.println("│     TOKEN: " + setToken);
                System.out.println("│    COOKIE: " + setCookie);
                System.out.println("└───────────────────────────────");

                handler.handle(jsonResponse);
            });
        });


        // Login data:

        String formData = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";

        req.putHeader("Content-Length", String.valueOf(formData.length()));

        req.end(formData);
    }

    public void send(TestContext context, HttpClient client, String clientId, JsonObject req, JsonObject res, Handler<Object> handler) {
        clientId = normalizeClientId(clientId);

        MultiMap headers = new CaseInsensitiveHeaders();

        headers.add(COOKIE, COOKIES.get(clientId));

        client.websocket(PORT, HOSTNAME, PATHNAME_WEBSOCKET, headers, ws -> {
            ws.handler(buffer -> {
                assertJsonEquals(context, res, new JsonObject(buffer.toString()), false);

                ws.close();

                handler.handle(null);
            });

            ws.write(Buffer.buffer(req.toString()));
        }, ws -> {
            context.assertTrue(false);

            handler.handle(null);
        });
    }

    public void chat(TestContext context, HttpClient client, String clientId, List<JsonObject> messages, Handler<Object> handler) {
        final String finalClientId = normalizeClientId(clientId);

        MultiMap headers = new CaseInsensitiveHeaders();

        headers.add(COOKIE, COOKIES.get(finalClientId));

        boolean startSending = messages.get(0).getString("action").equals("send");

        context.put("currentMessage" + finalClientId, startSending ? 1 : 0);

        // TODO: Add trace/verbose options!

        client.websocket(PORT, HOSTNAME, PATHNAME_WEBSOCKET, headers, ws -> {
            ws.handler(buffer -> {
                int currentMessage = context.get("currentMessage" + finalClientId);

                if (messages.get(currentMessage).getString("action").equals("recv")) {
                    System.out.println("\nRECV: " + finalClientId + ":\n" + messages.get(currentMessage).getJsonObject("value") + "\n" + new JsonObject(buffer.toString()));

                    assertJsonEquals(context, messages.get(currentMessage).getJsonObject("value"), new JsonObject(buffer.toString()), false);
                } else {
                    System.out.println("\nFAIL: " + finalClientId + ":\n" + currentMessage + "\n" + messages.get(currentMessage));

                    context.assertTrue(false);
                }

                ++currentMessage;

                if (currentMessage == messages.size()) {
                    ws.close();

                    handler.handle(null);
                } else {
                    context.put("currentMessage" + finalClientId, currentMessage);

                    while (currentMessage < messages.size() && messages.get(currentMessage).getString("action").equals("send")) {
                        System.out.println("\nSEND: " + finalClientId + ":\n" + messages.get(currentMessage).getJsonObject("value").toString());

                        ws.write(Buffer.buffer(messages.get(currentMessage).getJsonObject("value").toString()));

                        context.put("currentMessage" + finalClientId, ++currentMessage);
                    }
                }
            });

            if (startSending) {
                System.out.println("\nSTART: " + finalClientId + ":\n" + messages.get(0).getJsonObject("value").toString());

                ws.write(Buffer.buffer(messages.get(0).getJsonObject("value").toString()));
            } else {
                System.out.println("\nWAIT: " + finalClientId + "... ");
            }
        }, ws -> {
            context.assertTrue(false);

            handler.handle(null);
        });
    }

    // GETTERS:

    public int getPort() {
        return PORT;
    }
}
