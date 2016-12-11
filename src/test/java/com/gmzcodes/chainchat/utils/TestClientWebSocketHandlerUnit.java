package com.gmzcodes.chainchat.utils;

import com.gmzcodes.chainchat.store.SessionsStore;
import com.gmzcodes.chainchat.store.TokensStore;
import com.gmzcodes.chainchat.store.UsersStore;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;

/**
 * Created by danigamez on 11/12/2016.
 */
public class TestClientWebSocketHandlerUnit extends TestClient {
    private SessionsStore sessionsStore;
    private TokensStore tokensStore;
    private UsersStore usersStore;

    public TestClientWebSocketHandlerUnit(SessionsStore sessionsStore, TokensStore tokensStore, UsersStore usersStore) {
        this.sessionsStore = sessionsStore;
        this.tokensStore = tokensStore;
        this.usersStore = usersStore;
    }

    @Override
    public void login(TestContext context, HttpClient client, String username, String password, Handler<String> handler) {

    }

    @Override
    public void send(TestContext context, HttpClient client, String clientId, JsonObject req, JsonObject res, Handler<Object> handler) {

    }
}
