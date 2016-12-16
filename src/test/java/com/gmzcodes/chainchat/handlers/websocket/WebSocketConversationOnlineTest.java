package com.gmzcodes.chainchat.handlers.websocket;

import static com.gmzcodes.chainchat.constants.ExpectedValues.*;
import static com.gmzcodes.chainchat.utils.JsonAssert.assertJsonEquals;

import java.util.ArrayList;
import java.util.List;

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
public class WebSocketConversationOnlineTest {
    private TestSetupEndToEnd testSetup;
    private TestClient testClient;
    private HttpClient client;
    private int PORT;

    @Before
    public void setUp(TestContext context) {
        final Async async = context.async();

        testSetup = new TestSetupEndToEnd(context, ctx -> {
            // GET CLIENTS:

            testClient.login(context, client, USERNAME_ALICE, PASS_ALICE, loginResponseAliceJson -> {
                assertJsonEquals(context, ExpectedValues.USER_ALICE, loginResponseAliceJson, false);

                testClient.login(context, client, USERNAME_BOB, PASS_BOB, loginResponseBobJson -> {
                    assertJsonEquals(context, ExpectedValues.USER_BOB, loginResponseBobJson, false);

                    async.complete();
                });
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
    public void keepAConversationTest(TestContext context) {
        final Async async = context.async();

        // Messages:

        JsonObject msgAlice = testClient.getGenericMessage(USERNAME_ALICE, USERNAME_BOB);
        JsonObject msgBob = testClient.getGenericMessage(USERNAME_BOB, USERNAME_ALICE);

        // Server stored ACKS:

        JsonObject ackServerToAlice1 = new JsonObject()
                .put("type", "stored")
                .put("value", USERNAME_ALICE + "::" + msgAlice.getString("timestamp")); // TODO: Use helper function!
        JsonObject ackServerToAlice2 = new JsonObject()
                .put("type", "stored")
                .put("value", USERNAME_ALICE + "::" + msgAlice.getString("timestamp") + ".1"); // TODO: Use helper function!

        JsonObject ackServerToBob1 = new JsonObject()
                .put("type", "stored")
                .put("value", USERNAME_BOB + "::" + msgAlice.getString("timestamp")); // TODO: Use helper function!
        JsonObject ackServerToBob2 = new JsonObject()
                .put("type", "stored")
                .put("value", USERNAME_BOB + "::" + msgAlice.getString("timestamp") + ".1"); // TODO: Use helper function!

        // Stored Messages:

        JsonObject storedMessageAlice1 = msgAlice.copy().put("id", "alice::2016.10.10.12.00.00.000");
        storedMessageAlice1.remove("token");

        JsonObject storedMessageAlice2 = msgAlice.copy().put("id", "alice::2016.10.10.12.00.00.000.1");
        storedMessageAlice2.remove("token");

        JsonObject storedMessageBob1 = msgBob.copy().put("id", "bob::2016.10.10.12.00.00.000");
        storedMessageBob1.remove("token");

        JsonObject storedMessageBob2 = msgBob.copy().put("id", "bob::2016.10.10.12.00.00.000.1");
        storedMessageBob2.remove("token");

        // Received ACKS:

        JsonObject ackBobToAlice1 = msgBob.copy().put("type", "ack").put("value", storedMessageAlice1.getString("id"));
        JsonObject ackBobToAlice2 = msgBob.copy().put("type", "ack").put("value", storedMessageAlice2.getString("id"));

        JsonObject ackAliceToBob1 = msgAlice.copy().put("type", "ack").put("value", storedMessageBob1.getString("id"));
        JsonObject ackAliceToBob2 = msgAlice.copy().put("type", "ack").put("value", storedMessageBob2.getString("id"));

        // Server received ACKS:

        JsonObject ackServerBobToAlice1 = new JsonObject()
                .put("type", "ack")
                .put("from", "bob")
                .put("value", storedMessageAlice1.getString("id")); // TODO: Use helper function!
        JsonObject ackServerBobToAlice2 = new JsonObject()
                .put("type", "ack")
                .put("from", "bob")
                .put("value", storedMessageAlice2.getString("id")); // TODO: Use helper function!

        JsonObject ackServerAliceToBob1 = new JsonObject()
                .put("type", "ack")
                .put("from", "alice")
                .put("value", storedMessageBob1.getString("id")); // TODO: Use helper function!
        JsonObject ackServerAliceToBob2 = new JsonObject()
                .put("type", "ack")
                .put("from", "alice")
                .put("value", storedMessageBob2.getString("id")); // TODO: Use helper function!

        // Seen ACKS:

        JsonObject seenBobToAlice1 = msgBob.copy().put("type", "seen").put("value", storedMessageAlice1.getString("id"));
        JsonObject seenBobToAlice2 = msgBob.copy().put("type", "seen").put("value", storedMessageAlice2.getString("id"));

        JsonObject seenAliceToBob1 = msgAlice.copy().put("type", "seen").put("value", storedMessageBob1.getString("id"));
        JsonObject seenAliceToBob2 = msgAlice.copy().put("type", "seen").put("value", storedMessageBob2.getString("id"));

        // Server seen ACKS:

        JsonObject seenServerBobToAlice1 = new JsonObject()
                .put("type", "seen")
                .put("from", "bob")
                .put("value", storedMessageAlice1.getString("id")); // TODO: Use helper function!
        JsonObject seenServerBobToAlice2 = new JsonObject()
                .put("type", "seen")
                .put("from", "bob")
                .put("value", storedMessageAlice2.getString("id")); // TODO: Use helper function!

        JsonObject seenServerAliceToBob1 = new JsonObject()
                .put("type", "seen")
                .put("from", "alice")
                .put("value", storedMessageBob1.getString("id")); // TODO: Use helper function!
        JsonObject seenServerAliceToBob2 = new JsonObject()
                .put("type", "seen")
                .put("from", "alice")
                .put("value", storedMessageBob2.getString("id")); // TODO: Use helper function!

        // Conversation Alice:

        List<JsonObject> conversationAlice = new ArrayList<>();

        conversationAlice.add(new JsonObject().put("action", "send").put("value", msgAlice));
        conversationAlice.add(new JsonObject().put("action", "recv").put("value", ackServerToAlice1));
        conversationAlice.add(new JsonObject().put("action", "recv").put("value", ackServerBobToAlice1));
        conversationAlice.add(new JsonObject().put("action", "recv").put("value", seenServerBobToAlice1));

        conversationAlice.add(new JsonObject().put("action", "recv").put("value", storedMessageBob1));
        conversationAlice.add(new JsonObject().put("action", "send").put("value", ackAliceToBob1));
        conversationAlice.add(new JsonObject().put("action", "send").put("value", seenAliceToBob1));

        conversationAlice.add(new JsonObject().put("action", "send").put("value", msgAlice));
        conversationAlice.add(new JsonObject().put("action", "recv").put("value", ackServerToAlice2));
        conversationAlice.add(new JsonObject().put("action", "recv").put("value", ackServerBobToAlice2));
        conversationAlice.add(new JsonObject().put("action", "recv").put("value", seenServerBobToAlice2));

        conversationAlice.add(new JsonObject().put("action", "recv").put("value", storedMessageBob2));
        conversationAlice.add(new JsonObject().put("action", "send").put("value", ackAliceToBob2));
        conversationAlice.add(new JsonObject().put("action", "send").put("value", seenAliceToBob2));

        // Conversation Bob:

        List<JsonObject> conversationBob = new ArrayList<>();

        conversationBob.add(new JsonObject().put("action", "recv").put("value", storedMessageAlice1));
        conversationBob.add(new JsonObject().put("action", "send").put("value", ackBobToAlice1));
        conversationBob.add(new JsonObject().put("action", "send").put("value", seenBobToAlice1));

        conversationBob.add(new JsonObject().put("action", "send").put("value", msgBob));
        conversationBob.add(new JsonObject().put("action", "recv").put("value", ackServerToBob1));
        conversationBob.add(new JsonObject().put("action", "recv").put("value", ackServerAliceToBob1));
        conversationBob.add(new JsonObject().put("action", "recv").put("value", seenServerAliceToBob1));

        conversationBob.add(new JsonObject().put("action", "recv").put("value", storedMessageAlice2));
        conversationBob.add(new JsonObject().put("action", "send").put("value", ackBobToAlice2));
        conversationBob.add(new JsonObject().put("action", "send").put("value", seenBobToAlice2));

        conversationBob.add(new JsonObject().put("action", "send").put("value", msgBob));
        conversationBob.add(new JsonObject().put("action", "recv").put("value", ackServerToBob2));
        conversationBob.add(new JsonObject().put("action", "recv").put("value", ackServerAliceToBob2));
        conversationBob.add(new JsonObject().put("action", "recv").put("value", seenServerAliceToBob2));

        testClient.chat(context, client, USERNAME_BOB, conversationBob, done -> {
            /*
            assertJsonEquals(context, ExpectedValues.USER_BOB, loginResponseJson, false);

            context.assertNotNull(loginResponseJson.getJsonObject("conversations"));
            context.assertEquals(1, loginResponseJson.getJsonObject("conversations").size());
            context.assertNotNull(loginResponseJson.getJsonObject("conversations").getJsonArray("alice"));
            context.assertEquals(2, loginResponseJson.getJsonObject("conversations").getJsonArray("alice").size());
            context.assertEquals(storedReq1, loginResponseJson.getJsonObject("conversations").getJsonArray("alice").getJsonObject(0));
            context.assertEquals(storedReq2, loginResponseJson.getJsonObject("conversations").getJsonArray("alice").getJsonObject(1));
            */

            async.complete();
        });

        testClient.chat(context, client, USERNAME_ALICE, conversationAlice, done -> {
            /*
            context.assertEquals(2, testSetup.getConversationsStore().get(USERNAME_ALICE, USERNAME_BOB).toJson().size());

            JsonObject storedReq1 = req.copy().put("id", "alice::2016.10.10.12.00.00.000");
            storedReq1.remove("token");
            JsonObject storedReq2 = req.copy().put("id", "alice::2016.10.10.12.00.00.000.1");
            storedReq2.remove("token");

            context.assertEquals(storedReq1, testSetup.getConversationsStore().get(USERNAME_ALICE, USERNAME_BOB).toJson().getJsonObject(0));
            context.assertEquals(storedReq2, testSetup.getConversationsStore().get(USERNAME_ALICE, USERNAME_BOB).toJson().getJsonObject(1));
            */

            async.complete();
        });
    }
}
