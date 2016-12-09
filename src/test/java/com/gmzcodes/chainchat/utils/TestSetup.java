package com.gmzcodes.chainchat.utils;

import java.io.IOException;
import java.net.ServerSocket;

import com.gmzcodes.chainchat.PhilTheServer;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;

/**
 * Created by danigamez on 09/12/2016.
 */
public class TestSetup {
    // Test tooling:

    private int PORT;
    private HttpClient client;
    private TestClient testClient;

    public TestSetup(TestContext context, Handler<AsyncResult<String>> ctx) {
        try {
            // Try to get a random PORT:

            ServerSocket socket = new ServerSocket(0);

            PORT = socket.getLocalPort();
            testClient = new TestClient(PORT); // Set port in the Test Client instance.

            socket.close();
        } catch (IOException e) {
            // Abort if we could not set PORT:

            context.assertTrue(false);

            return;
        }

        // Vertx and HTTP client:

        Vertx vertx = Vertx.vertx();

        client = vertx.createHttpClient();

        // Deploy and wait for Phil...

        JsonObject config = new JsonObject().put("http.port", PORT);

        DeploymentOptions options = new DeploymentOptions()
                .setConfig(config) // Set config.
                .setWorker(true); // Prevents thread blocked WARNING in tests.

        vertx.deployVerticle(new PhilTheServer(), options, ctx); // Will notify child class.
    }

    public void kill(Handler<AsyncResult<Void>> ctx) {
        client.close();

        // TODO: philTheServer.stop(...)?
        // TODO: Vertx.vertx().undeploy(...)?

        Vertx.vertx().close(ctx);
    }

    public int getPort() {
        return PORT;
    }

    public HttpClient getClient() {
        return client;
    }

    public TestClient getTestClient() {
        return testClient;
    }
}
