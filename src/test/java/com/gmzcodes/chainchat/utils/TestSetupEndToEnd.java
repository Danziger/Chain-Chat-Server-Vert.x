package com.gmzcodes.chainchat.utils;

import static com.gmzcodes.chainchat.constants.DefaultStoresValues.*;

import java.io.IOException;
import java.net.ServerSocket;

import org.powermock.reflect.Whitebox;

import com.gmzcodes.chainchat.PhilTheServer;
import com.gmzcodes.chainchat.store.*;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;

/**
 * Created by danigamez on 09/12/2016.
 */
public class TestSetupEndToEnd extends TestSetup {
    // Test subject:

    private final PhilTheServer philTheServer = new PhilTheServer();

    // Test client:

    private TestClient testClient;

    // TEST SETUP END TO END SPECIFIC:

    private int PORT;
    private HttpClient client;

    // CONSTRUCTORS:

    public TestSetupEndToEnd(TestContext context, Handler<AsyncResult<String>> ctx) {
        this(context, ctx, true);
    }

    public TestSetupEndToEnd(TestContext context, Handler<AsyncResult<String>> ctx, boolean reinjectStores) {
        final BotsStore BOTS_STORE = BOTS_STORE();
        final ConversationsStore CONVERSATIONS_STORE = CONVERSATIONS_STORE();
        final SessionsStore SESSIONS_STORE = SESSIONS_STORE();
        final TokensStore TOKENS_STORE = TOKENS_STORE();
        final UsersStore USERS_STORE = USERS_STORE();
        final WebSocketsStore WEB_SOCKETS_STORE = WEB_SOCKETS_STORE();

        try {
            // Try to get a random PORT:

            ServerSocket socket = new ServerSocket(0);

            PORT = socket.getLocalPort();
            testClient = new TestClientEndToEnd(PORT); // Set port in the Test Client instance.

            socket.close();
        } catch (IOException e) {
            // Abort if we could not set PORT:

            context.assertTrue(false);

            return;
        }

        // Vertx and HTTP client:

        Vertx vertx = Vertx.vertx();

        client = vertx.createHttpClient();

        // Deploy and wait for Phil...

        JsonObject config = new JsonObject().put("http.port", PORT);

        DeploymentOptions options = new DeploymentOptions()
                .setConfig(config) // Set config.
                .setWorker(true); // Prevents thread blocked WARNING in tests.

        if (reinjectStores) {
            setBotsStore(BOTS_STORE);
            setConversationsStore(CONVERSATIONS_STORE);
            setSessionsStore(SESSIONS_STORE);
            setTokensStore(TOKENS_STORE);
            setUsersStore(USERS_STORE);
            setWebSocketsStore(WEB_SOCKETS_STORE);
        }

        vertx.deployVerticle(philTheServer, options, ctx); // Will notify child class.
    }

    public void kill(Handler<AsyncResult<Void>> ctx) {
        client.close();

        // TODO: philTheServer.stop(...)?
        // TODO: Vertx.vertx().undeploy(...)?

        Vertx.vertx().close(ctx);
    }

    // STORES HANDLING:

    public void setBotsStore(BotsStore botsStore) {
        Whitebox.setInternalState(philTheServer, "botsStore", botsStore);
    }

    public void setConversationsStore(ConversationsStore conversationsStore) {
        Whitebox.setInternalState(philTheServer, "conversationsStore", conversationsStore);
    }

    public void setSessionsStore(SessionsStore sessionsStore) {
        Whitebox.setInternalState(philTheServer, "sessionsStore", sessionsStore);
    }

    public void setTokensStore(TokensStore tokensStore) {
        Whitebox.setInternalState(philTheServer, "tokensStore", tokensStore);
    }

    public void setUsersStore(UsersStore usersStore) {
        Whitebox.setInternalState(philTheServer, "usersStore", usersStore);
    }

    public void setWebSocketsStore(WebSocketsStore webSocketsStore) {
        Whitebox.setInternalState(philTheServer, "webSocketsStore", webSocketsStore);
    }

    public BotsStore getBotsStore() {
        return Whitebox.getInternalState(philTheServer, "botsStore");
    }

    public ConversationsStore getConversationsStore() {
        return Whitebox.getInternalState(philTheServer, "conversationsStore");
    }

    public SessionsStore getSessionsStore() {
        return Whitebox.getInternalState(philTheServer, "sessionsStore");
    }

    public TokensStore getTokensStore() {
        return Whitebox.getInternalState(philTheServer, "tokensStore");
    }

    public UsersStore getUsersStore() {
        return Whitebox.getInternalState(philTheServer, "usersStore");
    }

    public WebSocketsStore getWebSocketsStore() {
        return Whitebox.getInternalState(philTheServer, "webSocketsStore");
    }

    // TEST SETUP END TO END SPECIFIC:

    public int getPort() {
        return PORT;
    }

    public HttpClient getClient() {
        return client;
    }

    public TestClient getTestClient() {
        return testClient;
    }
}
