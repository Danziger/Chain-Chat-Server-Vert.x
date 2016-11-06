package com.gmzcodes.chainchat.routes.ws;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.gmzcodes.chainchat.PhilTheServer;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicReference;

import static io.vertx.core.http.HttpHeaders.COOKIE;
import static io.vertx.core.http.HttpHeaders.SET_COOKIE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Raul on 12/10/2016.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
@PrepareForTest({ PhilTheServer.class, AsyncResult.class })
public class WebSocketRoutesTest {
    protected AtomicReference<String> cookies = new AtomicReference<>();

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
                    //context.asyncAssertSuccess();
                    context.assertEquals(body.toString(), "{}");

                    async.complete();
                });
            });


            // Login data:

            String formData = "{ \"username\": \"alice\", \"password\": \"alice1234\" }";

            req.putHeader("Content-Length", String.valueOf(formData.length()));

            req.end(formData);
        });
    }

    @Before
    public void setUp2(TestContext context) {
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
                    //context.asyncAssertSuccess();
                    context.assertEquals(body.toString(), "{}");

                    async.complete();
                });
            });

            // Login data:

            String formData = "{ \"username\": \"bob\", \"password\": \"bob1234\" }";

            req.putHeader("Content-Length", String.valueOf(formData.length()));

            req.end(formData);
        });
    }

    @After
    public void tearDown(TestContext context) {
        client.close();

        vertx.close(context.asyncAssertSuccess());
    }
/*
** Este test debría ser para comprobar el funcionamiento del WS
    @Test
    public void userOnlineTest(TestContext context) {
        final Async async = context.async();

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add(COOKIE, cookies.get());

        HttpClient httpClient = client.websocket(PORT, "localhost", "/myapp", headers, ws -> {

            ws.handler(buffer -> {
                System.out.println(buffer.toString());
                JsonObject message = new JsonObject(buffer.toString());

                assertTrue(message.containsKey("type"));
                assertEquals("tim", message.getString("type"));

                ws.close();

                async.complete();
            });

            ws.write(Buffer.buffer("{ \"type\": \"test\"}"));
        });
    }
*/

    @Test
    public void userOnlineTest(TestContext context) {
        final Async async = context.async();

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add(COOKIE, cookies.get());

        HttpClient httpClient = client.websocket(PORT, "localhost", "/myapp", headers, ws -> {

            ws.handler(buffer -> {
                System.out.println(buffer.toString());
                JsonObject message = new JsonObject(buffer.toString());

                assertTrue(message.containsKey("status"));
                assertEquals("online", message.getString("status"));

                ws.close();

                async.complete();
            });

            ws.write(Buffer.buffer("{ \"type\": \"test\", \"dst\": \"bob\", \"src\": \"alice\"}"));
        });
    }


    // TODO: Hacerlo por tiempo.
    @Test
    public void userOfflineTest(TestContext context) {
        final Async async = context.async();

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add(COOKIE, cookies.get());

        HttpClient httpClient = client.websocket(PORT, "localhost", "/eventbus", headers, ws -> {
            ws.write(Buffer.buffer("{\"type\":\"test\"}"));

            ws.handler(buffer -> {
                JsonObject message = new JsonObject(buffer.toString());

                assertTrue(message.containsKey("status"));
                assertEquals("off", message.getString("status"));

                ws.close();

                async.complete();
            });
        });
    }
}
