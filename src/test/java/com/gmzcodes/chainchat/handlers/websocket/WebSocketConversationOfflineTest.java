package com.gmzcodes.chainchat.handlers.websocket;

import static com.gmzcodes.chainchat.constants.ExpectedValues.*;
import static com.gmzcodes.chainchat.utils.JsonAssert.assertJsonEquals;
import static org.junit.Assert.assertEquals;

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
public class WebSocketConversationOfflineTest {
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
    public void oneMessageSentStoredAckAndLoginAfterwardsTest(TestContext context) {
        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, USERNAME_BOB);
        JsonObject ackResponse = new JsonObject()
                .put("type", "stored")
                .put("value", USERNAME_ALICE + "::" + req.getString("timestamp")); // TODO: Use function!

        testClient.send(context, client, USERNAME_ALICE, req, ackResponse, done -> {
            assertEquals(1, testSetup.getConversationsStore().get(USERNAME_ALICE, USERNAME_BOB).toJson().size());

            // TODO: All these should be better handled in the EXPECTED_VALUE constants!

            testClient.login(context, client, USERNAME_BOB, PASS_BOB, loginResponseJson -> {
                assertJsonEquals(context, ExpectedValues.USER_BOB, loginResponseJson, false);

                JsonObject storedReq1 = req.copy().put("id", "alice::2016.10.10.12.00.00.000");
                storedReq1.remove("token");

                context.assertNotNull(loginResponseJson.getJsonObject("conversations"));
                context.assertEquals(1, loginResponseJson.getJsonObject("conversations").size());
                context.assertNotNull(loginResponseJson.getJsonObject("conversations").getJsonArray("alice"));
                context.assertEquals(1, loginResponseJson.getJsonObject("conversations").getJsonArray("alice").size());
                context.assertEquals(storedReq1, loginResponseJson.getJsonObject("conversations").getJsonArray("alice").getJsonObject(0));

                async.complete();
            });
        });
    }

    @Test
    public void oneChatStoredAckAndLoginAfterwardsTest(TestContext context) {
        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, USERNAME_BOB);
        JsonObject ackResponse1 = new JsonObject()
                .put("type", "stored")
                .put("value", USERNAME_ALICE + "::" + req.getString("timestamp")); // TODO: Use helper function!
        JsonObject ackResponse2 = new JsonObject()
                .put("type", "stored")
                .put("value", USERNAME_ALICE + "::" + req.getString("timestamp") + ".1"); // TODO: Use helper function!

        List<JsonObject> conversation = new ArrayList<>();

        conversation.add(new JsonObject().put("action", "send").put("value", req));
        conversation.add(new JsonObject().put("action", "recv").put("value", ackResponse1));
        conversation.add(new JsonObject().put("action", "send").put("value", req));
        conversation.add(new JsonObject().put("action", "recv").put("value", ackResponse2));

        testClient.chat(context, client, USERNAME_ALICE, conversation, done -> {
            context.assertEquals(2, testSetup.getConversationsStore().get(USERNAME_ALICE, USERNAME_BOB).toJson().size());

            JsonObject storedReq1 = req.copy().put("id", "alice::2016.10.10.12.00.00.000");
            storedReq1.remove("token");
            JsonObject storedReq2 = req.copy().put("id", "alice::2016.10.10.12.00.00.000.1");
            storedReq2.remove("token");

            context.assertEquals(storedReq1, testSetup.getConversationsStore().get(USERNAME_ALICE, USERNAME_BOB).toJson().getJsonObject(0));
            context.assertEquals(storedReq2, testSetup.getConversationsStore().get(USERNAME_ALICE, USERNAME_BOB).toJson().getJsonObject(1));

            // TODO: All these should be better handled in the EXPECTED_VALUE constants!

            testClient.login(context, client, USERNAME_BOB, PASS_BOB, loginResponseJson -> {
                assertJsonEquals(context, ExpectedValues.USER_BOB, loginResponseJson, false);

                context.assertNotNull(loginResponseJson.getJsonObject("conversations"));
                context.assertEquals(1, loginResponseJson.getJsonObject("conversations").size());
                context.assertNotNull(loginResponseJson.getJsonObject("conversations").getJsonArray("alice"));
                context.assertEquals(2, loginResponseJson.getJsonObject("conversations").getJsonArray("alice").size());
                context.assertEquals(storedReq1, loginResponseJson.getJsonObject("conversations").getJsonArray("alice").getJsonObject(0));
                context.assertEquals(storedReq2, loginResponseJson.getJsonObject("conversations").getJsonArray("alice").getJsonObject(1));

                async.complete();
            });
        });
    }
}
