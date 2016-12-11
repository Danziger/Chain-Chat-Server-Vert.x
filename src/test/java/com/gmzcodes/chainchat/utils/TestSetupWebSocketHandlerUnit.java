package com.gmzcodes.chainchat.utils;

import static com.gmzcodes.chainchat.constants.DefaultStoresValues.*;

import org.powermock.reflect.Whitebox;

import com.gmzcodes.chainchat.handlers.websocket.WebSocketHandler;
import com.gmzcodes.chainchat.store.*;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.unit.TestContext;

/**
 * Created by danigamez on 11/12/2016.
 */
public class TestSetupWebSocketHandlerUnit extends TestSetup {
    // Test subject:

    private final WebSocketHandler webSocketHandler = new WebSocketHandler(
            BOTS_STORE,
            CONVERSATIONS_STORE,
            SESSIONS_STORE,
            TOKENS_STORE,
            USERS_STORE,
            WEB_SOCKETS_STORE
    );

    // Test client:

    private final TestClient testClient;

    // CONSTRUCTORS:

    public TestSetupWebSocketHandlerUnit(TestContext context, Handler<AsyncResult<String>> ctx) {
        // This client needs to fake the logging data in the stores, therefor it needs a reference to them:

        testClient = new TestClientWebSocketHandlerUnit(
                SESSIONS_STORE,
                TOKENS_STORE,
                USERS_STORE
        );

        // Done like this JUST to match signature from TestSetupEndToEnd:

        ctx.handle(new AsyncResult<String>() {
            @Override
            public String result() {
                return null;
            }

            @Override
            public Throwable cause() {
                return null;
            }

            @Override
            public boolean succeeded() {
                return false;
            }

            @Override
            public boolean failed() {
                return false;
            }
        });
    }

    public void kill(Handler<AsyncResult<Void>> ctx) {
        // Done like this JUST to match signature from TestSetupEndToEnd:

        ctx.handle(new AsyncResult<Void>() {
            @Override
            public Void result() {
                return null;
            }

            @Override
            public Throwable cause() {
                return null;
            }

            @Override
            public boolean succeeded() {
                return false;
            }

            @Override
            public boolean failed() {
                return false;
            }
        });
    }

    // STORES HANDLING:

    public void setBotsStore(BotsStore botsStore) {
        Whitebox.setInternalState(webSocketHandler, "botsStore", botsStore);
    }

    public void setConversationsStore(ConversationsStore conversationsStore) {
        Whitebox.setInternalState(webSocketHandler, "conversationsStore", conversationsStore);
    }

    public void setSessionsStore(SessionsStore sessionsStore) {
        Whitebox.setInternalState(webSocketHandler, "sessionsStore", sessionsStore);
    }

    public void setTokensStore(TokensStore tokensStore) {
        Whitebox.setInternalState(webSocketHandler, "tokensStore", tokensStore);
    }

    public void setUsersStore(UsersStore usersStore) {
        Whitebox.setInternalState(webSocketHandler, "usersStore", usersStore);
    }

    public void setWebSocketsStore(WebSocketsStore webSocketsStore) {
        Whitebox.setInternalState(webSocketHandler, "webSocketsStore", webSocketsStore);
    }
}
