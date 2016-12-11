package com.gmzcodes.chainchat.utils;

import java.util.HashMap;
import java.util.Map;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;

/**
 * Created by danigamez on 11/12/2016.
 */
public abstract class TestClient {
    protected final Map<String, String> COOKIES = new HashMap<>();
    protected final Map<String, String> TOKENS = new HashMap<>();

    // MAIN FUNCTIONALITY:

    public abstract void login(TestContext context, HttpClient client, String username, String password, Handler<String> handler);
    public abstract void send(TestContext context, HttpClient client, String clientId, JsonObject req, JsonObject res, Handler<Object> handler);

    // GETTERS:

    public String getCookies(String clientId) {
        clientId = normalizeClientId(clientId);

        return COOKIES.containsKey(clientId) ? COOKIES.get(clientId) : "";
    }

    public String getToken(String clientId) {
        clientId = normalizeClientId(clientId);

        return TOKENS.containsKey(clientId) ? TOKENS.get(clientId) : "";
    }

    // UTILS

    public final JsonObject getGenericMessage(String clientId, String to, String value) {
        clientId = normalizeClientId(clientId);

        return new JsonObject()
                .put("type", "msg")
                .put("timestamp", "2016.10.10.12.00.00.000")
                .put("username", getUsername(clientId))
                .put("token", TOKENS.get(clientId))
                .put("to", to)
                .put("value", value);

    }

    public final JsonObject getGenericMessage(String clientId, String to) {
        return getGenericMessage(clientId, to, "Hi!");
    }

    // CLASS HELPERS:

    protected String normalizeClientId(String clientId) {
        return clientId.contains("@") ? clientId : clientId + "@1";
    }

    protected String getUsername(String clientId) {
        return clientId.split("@\\d+")[0];
    }
}
