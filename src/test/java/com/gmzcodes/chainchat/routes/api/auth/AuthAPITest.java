package com.gmzcodes.chainchat;

import static com.gmzcodes.chainchat.utils.JsonAssert.assertJsonEquals;
import static io.vertx.core.http.HttpHeaders.COOKIE;
import static io.vertx.core.http.HttpHeaders.SET_COOKIE;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import com.gmzcodes.chainchat.constants.ExpectedValues;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
@PrepareForTest({ PhilTheServer.class, AsyncResult.class })
public class AuthAPITest {

    protected AtomicReference<String> cookies = new AtomicReference<>();

    private int PORT = 8081;

    private Vertx vertx;
    private PhilTheServer philTheServer;
    private HttpClient client;

    // TODO: Helper method/class for authenticated tests

    @Before
    public void setUp(TestContext context) {
        final Async async = context.async();

        vertx = Vertx.vertx();

        // Get random PORT:

        try {
            ServerSocket socket = new ServerSocket(0);

            PORT = socket.getLocalPort();

            socket.close();
        } catch (IOException e) { }

        // Create config JSON:

        JsonObject config = new JsonObject().put("http.port", PORT);

        // Create options object from config JSON:

        DeploymentOptions options = new DeploymentOptions().setConfig(config);

        // Create a client:

        client = vertx.createHttpClient();

        // Deploy Phil:

        philTheServer = new PhilTheServer();

        // Wait for Phil...

        vertx.deployVerticle(philTheServer, options, ctx -> {

            // Login handler:

            HttpClientRequest req = client.post(PORT, "localhost", "/api/auth", response -> {
                context.assertEquals(200, response.statusCode());

                String setCookie = response.headers().get(SET_COOKIE);

                context.assertNotNull(setCookie);

                cookies.set(setCookie); // Keep session cookie for later

                response.bodyHandler(body -> {
                    assertJsonEquals(context, ExpectedValues.USER_ALICE, body.toJsonObject(), false);

                    async.complete();
                });
            });

            // Login data:

            String formData = "{ \"username\": \"alice\", \"password\": \"alice1234\" }";

            req.putHeader("Content-Length", String.valueOf(formData.length()));

            req.end(formData);
        });
    }

    @After
    public void tearDown(TestContext context) {
        client.close();

        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void loginEmptyTest(TestContext context) throws Exception {
        final Async async = context.async();

        HttpClientRequest request = client.post(PORT, "localhost", "/api/auth", response -> {
            context.assertEquals(400, response.statusCode());

            async.complete();
        });

        request.end();
    }

    @Test
    public void loginIncorrectShortHeaderTest(TestContext context) throws Exception {
        final Async async = context.async();

        HttpClientRequest request = client.post(PORT, "localhost", "/api/auth", response -> {
            context.assertEquals(400, response.statusCode());

            async.complete();
        });

        String formData = "{ \"username\": \"alice\", \"password\": \"alice1234\" }";

        request.putHeader("Content-Length", "1");
        request.write(formData);

        request.end();
    }

    @Test(timeout=2000, expected = TimeoutException.class) // TODO: Add timeouts to all of them: http://stackoverflow.com/questions/16608934/can-i-apply-a-time-limit-for-all-the-tests-in-the-suite
    public void loginIncorrectLongHeaderTest(TestContext context) throws TimeoutException {
        final Async async = context.async();

        HttpClientRequest request = client.post(PORT, "localhost", "/api/auth", response -> {
            context.assertTrue(false, "This response should have never been made!");

            async.complete();
        });

        String formData = "{ \"username\": \"alice\", \"password\": \"alice1234\" }";

        request.putHeader("Content-Length", "512");
        request.write(formData);

        request.end();
    }

    @Test
    public void loginMalformedTest(TestContext context) throws Exception {
        final Async async = context.async();

        HttpClientRequest request = client.post(PORT, "localhost", "/api/auth", response -> {
            context.assertEquals(400, response.statusCode());

            async.complete();
        });

        String formData = "sdfsgfg";

        request.putHeader("Content-Length", String.valueOf(formData.length()));
        request.write(formData);

        request.end();
    }

    @Test
    public void loginFailedTest(TestContext context) throws Exception {
        final Async async = context.async();

        HttpClientRequest request = client.post(PORT, "localhost", "/api/auth", response -> {
            context.assertEquals(401, response.statusCode());

            async.complete();
        });

        String formData = "{ \"username\": \"alice\", \"password\": \"alice\" }";

        request.putHeader("Content-Length", String.valueOf(formData.length()));
        request.write(formData);

        request.end();
    }

    @Test
    public void loginSuccessfulTest(TestContext context) throws Exception {
        final Async async = context.async();

        HttpClientRequest request = client.post(PORT, "localhost", "/api/auth", response -> {
            context.assertEquals(200, response.statusCode());

            response.bodyHandler(body -> {
                // context.assertEquals(body.toString(), "{}");
                // TODO: Fix this test!

                async.complete();
            });
        });

        String formData = "{ \"username\": \"alice\", \"password\": \"alice1234\" }";

        request.putHeader("Content-Length", String.valueOf(formData.length()));
        request.write(formData);

        request.end();
    }

    @Test
    public void sessionCookieTest(TestContext context) throws Exception {
        final Async async = context.async();

        client.getNow(PORT, "localhost", "/", response -> {
            context.assertEquals(200, response.statusCode());

            context.assertTrue(response.headers().get("Set-Cookie").contains("vertx-web.session"));
            context.assertTrue(response.cookies().size() > 0);

            async.complete();
        });
    }

    @Test
    public void loginWorkingTest(TestContext context) throws Exception {
        final Async async = context.async();

        HttpClientRequest req = client.get(PORT, "localhost", "/api/user/contacts", response -> { // TODO: Change to /api/user
            context.assertEquals(200, response.statusCode());

            async.complete();
        });

        if (cookies.get() != null) {
            req.putHeader(COOKIE, cookies.get());

            req.end();
        } else {
            assertTrue("Cookie not found.", false);
        }
    }

    @Test
    public void loginRequiredTest(TestContext context) {
        final Async async = context.async();

        client.getNow(PORT, "localhost", "/api/user/osfsfjgl", response -> {
            context.assertEquals(403, response.statusCode());

            async.complete();
        });
    }

    @Test
    public void logInFailed(TestContext context) {
        final Async async = context.async();

        HttpClientRequest req = client.post(PORT, "localhost", "/api/auth", response -> {
            context.assertEquals(401, response.statusCode());

            async.complete();
        });

        // Login data:

        String formData = "{ \"username\": \"alice\", \"password\": \"alice\" }";

        req.putHeader("Content-Length", String.valueOf(formData.length()));

        req.end(formData);
    }

    @Test
    public void alreadyLoggedIn(TestContext context) {
        final Async async = context.async();

        HttpClientRequest req = client.post(PORT, "localhost", "/api/auth", response -> {
            context.assertEquals(304, response.statusCode());

            async.complete();
        });

        // Login data:

        String formData = "{ \"username\": \"alice\", \"password\": \"alice1234\" }";

        req.putHeader("Content-Length", String.valueOf(formData.length()));

        if (cookies.get() != null) {
            req.putHeader(COOKIE, cookies.get());
        } else {
            assertTrue("Cookie not found.", false);
        }

        req.end(formData);
    }

    @Test
    public void logoutSuccess(TestContext context) {
        final Async async = context.async();

        HttpClientRequest req = client.delete(PORT, "localhost", "/api/auth", response -> {
            context.assertEquals(200, response.statusCode());

            loginRequiredTest(context);

            assertEquals(0, response.cookies().size());

            async.complete();
        });

        if (cookies.get() != null) {
            req.putHeader(COOKIE, cookies.get());
        } else {
            assertTrue("Cookie not found.", false);
        }

        req.end();
    }

    @Test
    public void logoutFailed(TestContext context) {
        final Async async = context.async();

        HttpClientRequest req = client.delete(PORT, "localhost", "/api/auth", response -> {
            context.assertEquals(403, response.statusCode());

            async.complete();
        });

        req.end();
    }
}