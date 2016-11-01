package com.gmzcodes.chainchat;

import static io.vertx.core.http.HttpHeaders.COOKIE;
import static io.vertx.core.http.HttpHeaders.SET_COOKIE;
import static junit.framework.TestCase.assertTrue;

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

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
@PrepareForTest({ PhilTheServer.class, AsyncResult.class })
public class UserAPITest {

    protected AtomicReference<String> cookies = new AtomicReference<>();

    private int PORT = 8081;

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
            req.write(formData);

            req.end();
        });
    }

    @After
    public void tearDown(TestContext context) {
        client.close();

        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void API200UserContactsTest(TestContext context) throws Exception {
        final Async async = context.async();

        HttpClientRequest req = client.get(PORT, "localhost", "/api/user/contacts", response -> {
            context.assertEquals(200, response.statusCode());
            System.out.println(response.cookies());

            response.bodyHandler(body -> {
                JsonArray contacts = body.toJsonArray();

                context.assertTrue(contacts.size() == 0);

                async.complete();
            });
        });

        if (cookies.get() != null) {
            req.putHeader(COOKIE, cookies.get());

            req.end();
        } else {
            assertTrue("Cookie not found.", false);
        }
    }

    @Test
    public void API200UserFeedTest(TestContext context) throws Exception {
        final Async async = context.async();

        HttpClientRequest req = client.get(PORT, "localhost", "/api/user/feed", response -> {
            context.assertEquals(200, response.statusCode());

            response.bodyHandler(body -> {
                JsonObject feed = body.toJsonObject();

                context.assertEquals(feed.size(), 0);

                async.complete();
            });
        });

        if (cookies.get() != null) {
            req.putHeader(COOKIE, cookies.get());

            req.end();
        } else {
            assertTrue("Cookie not found.", false);
        }
    }


    @Test
    public void API403UserContactsTest(TestContext context) throws Exception {
        final Async async = context.async();

        client.getNow(PORT, "localhost", "/api/user/contacts", response -> {
            context.assertEquals(403, response.statusCode());
            async.complete();
        });
    }

    @Test
    public void API403UserFeedTest(TestContext context) throws Exception {
        final Async async = context.async();

        client.getNow(PORT, "localhost", "/api/user/feed", response -> {
            context.assertEquals(403, response.statusCode());
            async.complete();
        });
    }
}