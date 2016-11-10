package com.gmzcodes.chainchat.handlers.websocket;

import static com.gmzcodes.chainchat.utils.JsonAssert.assertJsonEquals;
import static io.vertx.core.http.HttpHeaders.COOKIE;
import static io.vertx.core.http.HttpHeaders.SET_COOKIE;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import com.gmzcodes.chainchat.PhilTheServer;
import com.gmzcodes.chainchat.constants.ExpectedValues;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

/**
 * Created by Raul on 12/10/2016.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
@PrepareForTest({ PhilTheServer.class, AsyncResult.class })
public class WebSocketHandlerTest {
    protected AtomicReference<String> cookies = new AtomicReference<>();
    protected AtomicReference<String> token = new AtomicReference<>();

    private int PORT = 8083;

    private Vertx vertx;
    private PhilTheServer philTheServer;
    private HttpClient client;


    // TODO: Helper method/class for authenticated tests

    @Before
    public void setUp(TestContext context) {
        final Async async = context.async();

        vertx = Vertx.vertx();

        // Get random PORT:

        try {
            ServerSocket socket = new ServerSocket(0);

            PORT = socket.getLocalPort();

            socket.close();
        } catch (IOException e) { }

        // Create config JSON:

        JsonObject config = new JsonObject().put("http.port", PORT);

        // Create options object from config JSON:

        DeploymentOptions options = new DeploymentOptions().setConfig(config);

        // Create a client:

        client = vertx.createHttpClient();

        // Deploy Phil:

        philTheServer = new PhilTheServer();

        // Wait for Phil...

        vertx.deployVerticle(philTheServer, options, ctx -> {

            // Login handler:

            HttpClientRequest req = client.post(PORT, "localhost", "/api/auth", response -> {
                context.assertEquals(200, response.statusCode());

                String setCookie = response.headers().get(SET_COOKIE);

                context.assertNotNull(setCookie);

                cookies.set(setCookie); // Keep session cookie for later

                response.bodyHandler(body -> {
                    JsonObject jsonResponse = body.toJsonObject();

                    assertJsonEquals(context, ExpectedValues.USER_ALICE, jsonResponse, false);

                    assertTrue(jsonResponse.containsKey("token"));

                    token.set(jsonResponse.getString("token"));

                    async.complete();
                });
            });


            // Login data:

            String formData = "{ \"username\": \"alice\", \"password\": \"alice1234\" }";

            req.putHeader("Content-Length", String.valueOf(formData.length()));

            req.end(formData);
        });
    }

    @After
    public void tearDown(TestContext context) {
        client.close();

        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void websocketWrongEndpointTest(TestContext context) {
        final Async async = context.async();

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add(COOKIE, cookies.get());

        HttpClient httpClient = client.websocket(PORT, "localhost", "/ws", headers, ws -> {
            ws.write(Buffer.buffer("{ \"type\": \"ping\" }"));
        }, ws -> {
            async.complete();
        });
    }

    @Test
    public void websocketMalformedMessageTest(TestContext context) {
        final Async async = context.async();

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add(COOKIE, cookies.get());

        HttpClient httpClient = client.websocket(PORT, "localhost", "/eventbus", headers, ws -> {
            ws.handler(buffer -> {
                JsonObject message = new JsonObject(buffer.toString());

                context.assertTrue(message.containsKey("type"));
                context.assertEquals("error", message.getString("type"));

                ws.close();

                async.complete();
            });

            ws.write(Buffer.buffer("{ \"type\": \"ping\" "));
        });
    }

    @Test
    public void websocketMissingTokenTest(TestContext context) {
        final Async async = context.async();

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add(COOKIE, cookies.get());

        HttpClient httpClient = client.websocket(PORT, "localhost", "/eventbus", headers, ws -> {
            ws.handler(buffer -> {
                JsonObject message = new JsonObject(buffer.toString());

                context.assertTrue(message.containsKey("type"));
                context.assertEquals("error", message.getString("type"));

                ws.close();

                async.complete();
            });

            ws.write(Buffer.buffer("{ \"type\": \"ping\", \"username\": \"alice\" }"));
        });
    }

    @Test
    public void websocketMissingUsernameTest(TestContext context) {
        final Async async = context.async();

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add(COOKIE, cookies.get());

        HttpClient httpClient = client.websocket(PORT, "localhost", "/eventbus", headers, ws -> {
            ws.handler(buffer -> {
                JsonObject message = new JsonObject(buffer.toString());

                context.assertTrue(message.containsKey("type"));
                context.assertEquals("error", message.getString("type"));

                ws.close();

                async.complete();
            });

            ws.write(Buffer.buffer("{ \"type\": \"ping\", \"token\": \"" + token + "\"}"));
        });
    }

    @Test
    public void websocketInvalidTokenTest(TestContext context) {
        final Async async = context.async();

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add(COOKIE, cookies.get());

        HttpClient httpClient = client.websocket(PORT, "localhost", "/eventbus", headers, ws -> {
            ws.handler(buffer -> {
                JsonObject message = new JsonObject(buffer.toString());

                context.assertTrue(message.containsKey("type"));
                context.assertEquals("error", message.getString("type"));

                ws.close();

                async.complete();
            });

            ws.write(Buffer.buffer("{ \"type\": \"ping\", \"username\": \"alice\", \"token\": \"1234\" }"));
        });
    }

    @Test
    public void websocketInvalidMessageTypeTest(TestContext context) {
        final Async async = context.async();

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add(COOKIE, cookies.get());

        HttpClient httpClient = client.websocket(PORT, "localhost", "/eventbus", headers, ws -> {
            ws.handler(buffer -> {
                JsonObject message = new JsonObject(buffer.toString());

                context.assertTrue(message.containsKey("type"));
                context.assertEquals("error", message.getString("type"));

                ws.close();

                async.complete();
            });

            ws.write(Buffer.buffer("{ \"type\": \"test\", \"username\": \"alice\", \"token\": \"" + token + "\" }"));
        });
    }

    @Test
    public void websocketInvalidUsernameTest(TestContext context) {
        final Async async = context.async();

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add(COOKIE, cookies.get());

        HttpClient httpClient = client.websocket(PORT, "localhost", "/eventbus", headers, ws -> {
            ws.handler(buffer -> {
                JsonObject message = new JsonObject(buffer.toString());

                context.assertTrue(message.containsKey("type"));
                context.assertEquals("error", message.getString("type"));

                ws.close();

                async.complete();
            });

            ws.write(Buffer.buffer("{ \"type\": \"ping\", \"username\": \"bob\", \"token\": \"" + token + "\" }"));
        });
    }

    @Test
    public void websocketPingPongTest(TestContext context) {
        final Async async = context.async();

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add(COOKIE, cookies.get());

        HttpClient httpClient = client.websocket(PORT, "localhost", "/eventbus", headers, ws -> {
            ws.handler(buffer -> {
                JsonObject message = new JsonObject(buffer.toString());

                context.assertTrue(message.containsKey("type"));
                context.assertEquals("pong", message.getString("type"));

                ws.close();

                async.complete();
            });

            ws.write(Buffer.buffer("{ \"type\": \"ping\", \"username\": \"alice\", \"token\": \"" + token + "\" }"));
        });
    }

    @Test
    public void websocketMissingToTest(TestContext context) {
        final Async async = context.async();

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add(COOKIE, cookies.get());

        HttpClient httpClient = client.websocket(PORT, "localhost", "/eventbus", headers, ws -> {
            ws.handler(buffer -> {
                JsonObject message = new JsonObject(buffer.toString());

                context.assertTrue(message.containsKey("type"));
                context.assertEquals("error", message.getString("type"));

                ws.close();

                async.complete();
            });

            ws.write(Buffer.buffer("{ \"type\": \"msg\", \"username\": \"alice\", \"token\": \"" + token + "\", \"value\": \"Hi\" }"));
        });
    }

    @Test
    public void websocketUnreachableContactTest(TestContext context) {
        final Async async = context.async();

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add(COOKIE, cookies.get());

        HttpClient httpClient = client.websocket(PORT, "localhost", "/eventbus", headers, ws -> {
            ws.handler(buffer -> {
                JsonObject message = new JsonObject(buffer.toString());

                context.assertTrue(message.containsKey("type"));
                context.assertEquals("error", message.getString("type"));

                ws.close();

                async.complete();
            });

            ws.write(Buffer.buffer("{ \"type\": \"msg\", \"username\": \"alice\", \"token\": \"" + token + "\", \"to\": \"dani\", \"value\": \"Hi\" }"));
        });
    }

    @Test
    public void websocketMessageOkTest(TestContext context) {
        final Async async = context.async();

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add(COOKIE, cookies.get());

        HttpClient httpClient = client.websocket(PORT, "localhost", "/eventbus", headers, ws -> {
            ws.handler(buffer -> {
                JsonObject message = new JsonObject(buffer.toString());

                context.assertTrue(message.containsKey("type"));
                context.assertEquals("ack", message.getString("type"));

                ws.close();

                async.complete();
            });

            ws.write(Buffer.buffer("{ \"type\": \"msg\", \"username\": \"alice\", \"token\": \"" + token + "\", \"to\": \"bob\", \"value\": \"Hi\" }"));
        });
    }
}
