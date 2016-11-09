package com.gmzcodes.chainchat;

import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

import java.io.IOException;
import java.net.ServerSocket;

import io.vertx.core.http.HttpClient;
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
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
@PrepareForTest({ PhilTheServer.class, AsyncResult.class })
public class PhilTheServerTest {

    private int PORT = 8081;

    private Vertx vertx;
    private PhilTheServer philTheServer;

    @Before
    public void setUp(TestContext context) {
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

        // Deploy our Verticle:

        philTheServer = new PhilTheServer();

        vertx.deployVerticle(philTheServer, options, context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void initialLoadTest(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(PORT, "localhost", "/", response -> {
            context.assertEquals(200, response.statusCode());
            context.assertEquals("text/html", response.headers().get("content-type"));

            response.bodyHandler(body -> {
                context.assertTrue(body.toString().contains("loader__base"));

                async.complete();
            });
        });
    }

    @Test
    public void notFoundTest(TestContext context) { // TODO: Duplicate method in the API...
        final Async async = context.async();

        vertx.createHttpClient().getNow(PORT, "localhost", "/whatever", response -> {
            context.assertEquals(404, response.statusCode());

            async.complete();
        });
    }

    @Test
    public void userImagesTest(TestContext context) throws Exception {
        final Async async = context.async();

        vertx.createHttpClient().getNow(PORT, "localhost", "/static/images/1212774.jpg", response -> {
            context.assertEquals(200, response.statusCode());
            context.assertEquals("image/jpeg", response.headers().get("content-type"));

            response.bodyHandler(body -> {
                context.assertTrue(body.getBytes().length > 0);

                async.complete();
            });
        });
    }
}