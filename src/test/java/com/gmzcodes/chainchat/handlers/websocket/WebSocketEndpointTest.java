package com.gmzcodes.chainchat.handlers.websocket;

import static com.gmzcodes.chainchat.constants.ExpectedValues.PASS_ALICE;
import static com.gmzcodes.chainchat.constants.ExpectedValues.USERNAME_ALICE;
import static com.gmzcodes.chainchat.constants.ServerConfigValues.HOSTNAME;
import static com.gmzcodes.chainchat.constants.ServerConfigValues.PATHNAME_WEBSOCKET;
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
import com.gmzcodes.chainchat.constants.ExpectedValues;
import com.gmzcodes.chainchat.utils.TestClient;
import com.gmzcodes.chainchat.utils.TestSetupEndToEnd;

import io.vertx.core.AsyncResult;
import io.vertx.core.MultiMap;
import io.vertx.core.http.CaseInsensitiveHeaders;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

/**
 * Created by Raul on 12/10/2016.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
@PrepareForTest({ PhilTheServer.class, AsyncResult.class })
public class WebSocketEndpointTest {
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
    public void wrongEndpointTest(TestContext context) {
        final Async async = context.async();

        MultiMap headers = new CaseInsensitiveHeaders()
                .add(COOKIE, testClient.getCookies(USERNAME_ALICE));

        client.websocket(PORT, HOSTNAME, "/ws", headers, ws -> { // Wrong endpoint.
            context.assertTrue(false); // Should never happen.
            async.complete();
        }, ws -> {
            context.assertTrue(true);
            async.complete();
        });
    }

    @Test
    public void rightEndpointTest(TestContext context) {
        final Async async = context.async();

        MultiMap headers = new CaseInsensitiveHeaders()
                .add(COOKIE, testClient.getCookies(USERNAME_ALICE));

        client.websocket(PORT, HOSTNAME, PATHNAME_WEBSOCKET, headers, ws -> { // Right endpoint.
            context.assertTrue(true);
            async.complete();
        }, ws -> {
            context.assertTrue(false); // Should never happen.
            async.complete();
        });
    }
}
