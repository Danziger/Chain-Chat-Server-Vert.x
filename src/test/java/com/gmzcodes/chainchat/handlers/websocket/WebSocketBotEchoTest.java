package com.gmzcodes.chainchat.handlers.websocket;

import static com.gmzcodes.chainchat.constants.ExpectedValues.PASS_ALICE;
import static com.gmzcodes.chainchat.constants.ExpectedValues.USERNAME_ALICE;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import com.gmzcodes.chainchat.PhilTheServer;
import com.gmzcodes.chainchat.utils.TestClient;
import com.gmzcodes.chainchat.utils.TestSetup;

import io.vertx.core.AsyncResult;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

/**
 * Created by danigamez on 09/12/2016.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
@PrepareForTest({ PhilTheServer.class, AsyncResult.class })
public class WebSocketBotEchoTest {
    private TestSetup testSetup;
    private TestClient testClient;
    private HttpClient client;
    private int PORT;

    @Before
    public void setUp(TestContext context) {
        final Async async = context.async();

        testSetup = new TestSetup(context, ctx -> {
            // GET CLIENTS:

            testClient.login(context, client, USERNAME_ALICE, PASS_ALICE, identifier -> {
                context.assertEquals("alice@1", identifier);

                async.complete();
            });
        });

        PORT = testSetup.getPort();
        client = testSetup.getClient();
        testClient = testSetup.getTestClient();
    }

    @After
    public void tearDown(TestContext context) {
        final Async async = context.async();

        testSetup.kill(ctx -> async.complete());
    }

    @Test
    public void echoTest(TestContext context) {
        /*
        final Async async = context.async();

        // TODO: Do one test class per bot!

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
        */
    }
}
