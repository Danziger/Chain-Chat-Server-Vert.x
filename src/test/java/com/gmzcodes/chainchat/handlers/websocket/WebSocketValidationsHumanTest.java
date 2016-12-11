package com.gmzcodes.chainchat.handlers.websocket;

import static com.gmzcodes.chainchat.constants.ExpectedValues.*;
import static com.gmzcodes.chainchat.contants.WebSocketErrorMessagesConstants.INVALID_DESTINATION;
import static com.gmzcodes.chainchat.contants.WebSocketErrorMessagesConstants.UNKNOWN_DESTINATION;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import com.gmzcodes.chainchat.PhilTheServer;
import com.gmzcodes.chainchat.utils.TestClient;
import com.gmzcodes.chainchat.utils.TestClientEndToEnd;
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
public class WebSocketValidationsHumanTest {
    private TestSetupEndToEnd testSetup;
    private TestClient testClient;
    private HttpClient client;

    @Before
    public void setUp(TestContext context) {
        final Async async = context.async();

        testSetup = new TestSetupEndToEnd(context, ctx -> {
            // GET CLIENTS:

            testClient.login(context, client, USERNAME_ALICE, PASS_ALICE, identifier -> {
                context.assertEquals("alice@1", identifier);

                async.complete();
            });
        });

        client = testSetup.getClient();
        testClient = testSetup.getTestClient();
    }

    @After
    public void tearDown(TestContext context) {
        final Async async = context.async();

        testSetup.kill(ctx -> async.complete());
    }

    @Test
    public void unknownContactTest(TestContext context) {
        // 10. UNKNOWN_DESTINATION

        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, "elliot");

        // Valid message, but Elliot not in Alice's contact list.

        testClient.send(context, client, USERNAME_ALICE, req, UNKNOWN_DESTINATION, done -> async.complete());
    }

    @Test
    public void invalidMissingContactTest(TestContext context) {
        // 11A. INVALID_DESTINATION (non-existing destination)

        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, "francis");

        // Valid message, but Francis doesn't exist.

        testClient.send(context, client, USERNAME_ALICE, req, INVALID_DESTINATION, done -> async.complete());
    }

    @Test
    public void invalidBlockedContactTest(TestContext context) {
        // 11B. INVALID_DESTINATION (blocked by destination)

        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, USERNAME_CHRIS);

        // Valid message, but Chris blocked Alice.

        testClient.send(context, client, USERNAME_ALICE, req, INVALID_DESTINATION, done -> async.complete());
    }
}
