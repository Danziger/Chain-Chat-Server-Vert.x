package com.gmzcodes.chainchat.handlers.websocket;

import static com.gmzcodes.chainchat.constants.ExpectedValues.*;
import static com.gmzcodes.chainchat.utils.JsonAssert.assertJsonEquals;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import com.gmzcodes.chainchat.PhilTheServer;
import com.gmzcodes.chainchat.constants.ExpectedValues;
import com.gmzcodes.chainchat.utils.TestClient;
import com.gmzcodes.chainchat.utils.TestSetupEndToEnd;

import io.vertx.core.AsyncResult;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
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
    private TestSetupEndToEnd testSetup;
    private TestClient testClient;
    private HttpClient client;
    private int PORT;

    @Before
    public void setUp(TestContext context) {
        final Async async = context.async();

        testSetup = new TestSetupEndToEnd(context, ctx -> {
            // GET CLIENTS:

            testClient.login(context, client, USERNAME_ALICE, PASS_ALICE, loginResponseJson -> {
                assertJsonEquals(context, ExpectedValues.USER_ALICE, loginResponseJson, false);

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
    public void botEchoNothing(TestContext context) {
        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, "@echobot");
        req.put("value", "");

        JsonObject response = new JsonObject()
                .put("type", "msg")
                .put("to", USERNAME_ALICE)
                .put("value", "")
                .put("id", "@echobot::2016.10.10.12.00.00.000")
                .put("username", "@echobot");

        testClient.send(context, client, USERNAME_ALICE, req, response, done -> {
            context.assertNotNull(testSetup.getConversationsStore().get(USERNAME_ALICE, "@echobot"));
            context.assertEquals(2, testSetup.getConversationsStore().get(USERNAME_ALICE, "@echobot").toJson().size());

            async.complete();
        });
    }

    @Test
    public void botEchoSomething(TestContext context) {
        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, "@echobot");
        req.put("value", "Something!");

        JsonObject response = new JsonObject()
                .put("type", "msg")
                .put("to", USERNAME_ALICE)
                .put("value", "Something!")
                .put("id", "@echobot::2016.10.10.12.00.00.000")
                .put("username", "@echobot");

        testClient.send(context, client, USERNAME_ALICE, req, response, done -> {
            context.assertNotNull(testSetup.getConversationsStore().get(USERNAME_ALICE, "@echobot"));
            context.assertEquals(2, testSetup.getConversationsStore().get(USERNAME_ALICE, "@echobot").toJson().size());

            async.complete();
        });
    }
}
