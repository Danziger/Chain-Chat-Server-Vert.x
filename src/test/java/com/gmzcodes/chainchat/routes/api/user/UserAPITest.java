package com.gmzcodes.chainchat;

import static com.gmzcodes.chainchat.constants.ExpectedValues.PASS_ALICE;
import static com.gmzcodes.chainchat.constants.ExpectedValues.USERNAME_ALICE;
import static com.gmzcodes.chainchat.utils.JsonAssert.assertJsonEquals;
import static io.vertx.core.http.HttpHeaders.COOKIE;
import static io.vertx.core.http.HttpHeaders.SET_COOKIE;
import static junit.framework.TestCase.assertTrue;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import com.gmzcodes.chainchat.constants.ExpectedValues;
import com.gmzcodes.chainchat.utils.TestClient;
import com.gmzcodes.chainchat.utils.TestSetupEndToEnd;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
@PrepareForTest({ PhilTheServer.class, AsyncResult.class })
public class UserAPITest {
    private TestSetupEndToEnd testSetup;
    private TestClient testClient;
    private HttpClient client;
    private int PORT;

    @Before
    public void setUp(TestContext context) {
        final Async async = context.async();

        // TODO: Configure testClient to test end to end or unit!

        testSetup = new TestSetupEndToEnd(context, ctx -> {
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
    public void API200UserContactsTest(TestContext context) throws Exception {
        final Async async = context.async();

        HttpClientRequest req = client.get(PORT, "localhost", "/api/user/contacts", response -> {
            context.assertEquals(200, response.statusCode());

            response.bodyHandler(body -> {
                JsonArray contacts = body.toJsonArray();

                context.assertTrue(contacts.size() == 0);

                async.complete();
            });
        });

        if (testClient.getCookies("alice") != null) {
            req.putHeader(COOKIE, testClient.getCookies("alice"));

            req.end();
        } else {
            assertTrue("Cookie not found.", false);
        }
    }

    @Test
    public void API200UserFeedTest(TestContext context) throws Exception {
        final Async async = context.async();

        HttpClientRequest req = client.get(PORT, "localhost", "/api/user/feed", response -> {
            context.assertEquals(200, response.statusCode());

            response.bodyHandler(body -> {
                JsonObject feed = body.toJsonObject();

                context.assertEquals(feed.size(), 0);

                async.complete();
            });
        });

        if (testClient.getCookies("alice") != null) {
            req.putHeader(COOKIE, testClient.getCookies("alice"));

            req.end();
        } else {
            assertTrue("Cookie not found.", false);
        }
    }


    @Test
    public void API403UserContactsTest(TestContext context) throws Exception {
        final Async async = context.async();

        client.getNow(PORT, "localhost", "/api/user/contacts", response -> {
            context.assertEquals(403, response.statusCode());
            async.complete();
        });
    }

    @Test
    public void API403UserFeedTest(TestContext context) throws Exception {
        final Async async = context.async();

        client.getNow(PORT, "localhost", "/api/user/feed", response -> {
            context.assertEquals(403, response.statusCode());
            async.complete();
        });
    }
}