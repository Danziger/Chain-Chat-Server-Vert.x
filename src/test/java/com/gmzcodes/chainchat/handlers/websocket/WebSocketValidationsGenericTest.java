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
public class WebSocketValidationsGenericTest {
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
    public void malformedMessageTest(TestContext context) {
        // 1. MALFORMED_JSON

        final Async async = context.async();

        MultiMap headers = new CaseInsensitiveHeaders();
        headers.add(COOKIE, testClient.getCookies(USERNAME_ALICE));

        HttpClient httpClient = client.websocket(PORT, HOSTNAME, PATHNAME_WEBSOCKET, headers, ws -> {
            ws.handler(buffer -> {
                assertJsonEquals(context, MALFORMED_JSON, new JsonObject(buffer.toString()), false);

                ws.close();

                async.complete();
            });

            ws.write(Buffer.buffer(testClient.getGenericMessage(USERNAME_ALICE, USERNAME_BOB).toString().substring(1))); // Malformed JSON.
        });
    }

    @Test
    public void missingTokenTest(TestContext context) {
        // 2A. MISSING_TOKEN (missing)

        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, USERNAME_BOB);

        req.remove("token"); // Missing token.

        testClient.send(context, client, USERNAME_ALICE, req, MISSING_TOKEN, done -> async.complete());
    }

    @Test
    public void emptyTokenTest(TestContext context) {
        // 2B. MISSING_TOKEN (empty)

        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, USERNAME_BOB);

        req.put("token", ""); // Empty token.

        testClient.send(context, client, USERNAME_ALICE, req, MISSING_TOKEN, done -> async.complete());
    }

    @Test
    public void missingOriginTest(TestContext context) {
        // 3A. MISSING_TOKEN (missing)

        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, USERNAME_BOB);

        // TODO: Rename to to

        req.remove("username"); // Missing username.

        testClient.send(context, client, USERNAME_ALICE, req, MISSING_ORIGIN, done -> async.complete());
    }

    @Test
    public void emptyOriginTest(TestContext context) {
        // 3B. MISSING_TOKEN (empty)

        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, USERNAME_BOB);

        // TODO: Rename to to

        req.put("username", ""); // Empty username.

        testClient.send(context, client, USERNAME_ALICE, req, MISSING_ORIGIN, done -> async.complete());
    }

    @Test
    public void missingDestinationTest(TestContext context) {
        // 4A. MISSING_DESTINATION (missing)

        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, USERNAME_BOB);

        req.remove("to"); // Missing to.

        testClient.send(context, client, USERNAME_ALICE, req, MISSING_DESTINATION, done -> async.complete());
    }

    @Test
    public void emptyDestinationTest(TestContext context) {
        // 4B. MISSING_DESTINATION (empty)

        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, USERNAME_BOB);

        req.put("to", ""); // Empty to.

        testClient.send(context, client, USERNAME_ALICE, req, MISSING_DESTINATION, done -> async.complete());
    }

    @Test
    public void missingTimestampTest(TestContext context) {
        // 5A. INVALID_TIMESTAMP (missing)

        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, USERNAME_BOB);

        req.remove("timestamp"); // Missing timestamp.

        testClient.send(context, client, USERNAME_ALICE, req, INVALID_TIMESTAMP, done -> async.complete());
    }

    @Test
    public void emptyTimestampTest(TestContext context) {
        // 5B. INVALID_TIMESTAMP (empty)

        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, USERNAME_BOB);

        req.put("timestamp", ""); // Empty timestamp.

        testClient.send(context, client, USERNAME_ALICE, req, INVALID_TIMESTAMP, done -> async.complete());
    }

    @Test
    public void invalidTimestampFormatTest(TestContext context) {
        // 5C. INVALID_TIMESTAMP (invalid format)

        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, USERNAME_BOB);

        req.put("timestamp", "2016.10.10.01.01.01"); // Invalid timestamp format (milliseconds missing).

        testClient.send(context, client, USERNAME_ALICE, req, INVALID_TIMESTAMP, done -> async.complete());
    }

    @Test
    public void invalidNonExistingTokenTest(TestContext context) {
        // 6A. INVALID_TOKEN (non-existing)

        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, USERNAME_BOB);

        req.put("token", "1234"); // Invalid (non-existing) token.

        testClient.send(context, client, USERNAME_ALICE, req, INVALID_TOKEN, done -> async.complete());
    }

    @Test
    public void invalidNonMatchingTokenTest(TestContext context) {
        // 6B. INVALID_TOKEN (non-matching)

        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, USERNAME_BOB);

        req.put("username", USERNAME_BOB); // Valid token was generated for Alice, not Bob, so now it will be invalid.

        testClient.send(context, client, USERNAME_ALICE, req, INVALID_TOKEN, done -> async.complete());
    }

    @Test
    public void blockedHumanDestinationTest(TestContext context) {
        // 7A. BLOCKED_DESTINATION (human)

        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, "dani");

        // Valid message, but Alice blocked Dani.

        testClient.send(context, client, USERNAME_ALICE, req, BLOCKED_DESTINATION, done -> async.complete());
    }

    @Test
    public void blockedBotDestinationTest(TestContext context) {
        // 7B. BLOCKED_DESTINATION (bot)

        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, "@blockedbot");

        // Valid message, but Alice blocked @blockedbot.

        testClient.send(context, client, USERNAME_ALICE, req, BLOCKED_DESTINATION, done -> async.complete());
    }

    @Test
    public void invalidMessageTypeTest(TestContext context) {
        // 12. INVALID_MESSAGE_TYPE

        final Async async = context.async();

        JsonObject req = testClient.getGenericMessage(USERNAME_ALICE, USERNAME_BOB);

        req.put("type", "invalid"); // Invalid type.

        testClient.send(context, client, USERNAME_ALICE, req, INVALID_MESSAGE_TYPE, done -> async.complete());
    }
}
