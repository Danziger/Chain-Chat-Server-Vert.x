package com.gmzcodes.chainchat.utils;

import java.util.List;

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
    public void login(TestContext context, HttpClient client, String username, String password, Handler<JsonObject> handler) {

    }

    @Override
    public void send(TestContext context, HttpClient client, String clientId, JsonObject req, JsonObject res, Handler<Object> handler) {

    }

    @Override
    public void chat(TestContext context, HttpClient client, String clientId, List<JsonObject> messages, Handler<Object> handler) {

    }
}
