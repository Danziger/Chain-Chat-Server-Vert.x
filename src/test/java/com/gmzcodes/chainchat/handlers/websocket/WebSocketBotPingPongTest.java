package com.gmzcodes.chainchat.handlers.websocket;

import static com.gmzcodes.chainchat.constants.ExpectedValues.PASS_ALICE;
import static com.gmzcodes.chainchat.constants.ExpectedValues.USERNAME_ALICE;
import static com.gmzcodes.chainchat.utils.JsonAssert.assertJsonEquals;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import com.gmzcodes.chainchat.PhilTheServer;
import com.gmzcodes.chainchat.bots.PingPongBot;
import com.gmzcodes.chainchat.constants.ExpectedValues;
import com.gmzcodes.chainchat.utils.TestClient;
import com.gmzcodes.chainchat.utils.TestSetupEndToEnd;

import io.vertx.core.AsyncResult;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import junit.framework.TestCase;

/**
 * Created by danigamez on 09/12/2016.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
@PrepareForTest({ PhilTheServer.class, AsyncResult.class })
public class WebSocketBotPingPongTest {
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
    public void botPingPongNothing(TestContext context) {
        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, "@pingpongbot");
        req.put("value", "");

        JsonObject response = new JsonObject()
                .put("type", "msg")
                .put("to", USERNAME_ALICE)
                .put("value", "Ping?")
                .put("id", "@pingpongbot::2016.10.10.12.00.00.000")
                .put("username", "@pingpongbot");

        testClient.send(context, client, USERNAME_ALICE, req, response, done -> {
            context.assertNotNull(testSetup.getConversationsStore().get(USERNAME_ALICE, "@pingpongbot"));
            assertEquals(2, testSetup.getConversationsStore().get(USERNAME_ALICE, "@pingpongbot").toJson().size());

            async.complete();
        });
    }

    @Test
    public void botPingPongRandomSomething(TestContext context) {
        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, "@pingpongbot");
        req.put("value", "Something!");

        JsonObject response = new JsonObject()
                .put("type", "msg")
                .put("to", USERNAME_ALICE)
                .put("value", "Ping?")
                .put("id", "@pingpongbot::2016.10.10.12.00.00.000")
                .put("username", "@pingpongbot");

        testClient.send(context, client, USERNAME_ALICE, req, response, done -> {
            context.assertNotNull(testSetup.getConversationsStore().get(USERNAME_ALICE, "@pingpongbot"));
            context.assertEquals(2, testSetup.getConversationsStore().get(USERNAME_ALICE, "@pingpongbot").toJson().size());

            async.complete();
        });
    }

    @Test
    public void botPingPongOk(TestContext context) {
        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, "@pingpongbot");
        req.put("value", "Ping");

        JsonObject response = new JsonObject()
                .put("type", "msg")
                .put("to", USERNAME_ALICE)
                .put("value", "Pong!")
                .put("id", "@pingpongbot::2016.10.10.12.00.00.000")
                .put("username", "@pingpongbot");

        testClient.send(context, client, USERNAME_ALICE, req, response, done -> {
            context.assertNotNull(testSetup.getConversationsStore().get(USERNAME_ALICE, "@pingpongbot"));
            context.assertEquals(2, testSetup.getConversationsStore().get(USERNAME_ALICE, "@pingpongbot").toJson().size());

            async.complete();
        });
    }
}
