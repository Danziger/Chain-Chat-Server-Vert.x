package com.gmzcodes.chainchat.handlers.websocket;

import static com.gmzcodes.chainchat.constants.ExpectedValues.*;
import static com.gmzcodes.chainchat.constants.ServerConfigValues.HOSTNAME;
import static com.gmzcodes.chainchat.constants.ServerConfigValues.PATHNAME_WEBSOCKET;
import static com.gmzcodes.chainchat.contants.WebSocketErrorMessagesConstants.*;
import static com.gmzcodes.chainchat.utils.JsonAssert.assertJsonEquals;
import static io.vertx.core.http.HttpHeaders.COOKIE;

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
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.CaseInsensitiveHeaders;
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
public class WebSocketValidationsBotTest {
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
    public void invalidBotTest(TestContext context) {
        // 8. INVALID_BOT

        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, "@invalidbot");

        // Valid message, but @invalidbot doesn't exists.

        testClient.send(context, client, USERNAME_ALICE, req, INVALID_BOT, done -> async.complete());
    }

    @Test
    public void blockedByBotTest(TestContext context) {
        // 9. BLOCKED_BY_BOT

        // TODO: This can only be unit-tested

        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, "@echobot");

        // Valid message, but @invalidbot doesn't exists.

        testClient.send(context, client, USERNAME_ALICE, req, BLOCKED_BY_BOT, done -> async.complete());
    }
}
