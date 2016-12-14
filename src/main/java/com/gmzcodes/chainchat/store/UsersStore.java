package com.gmzcodes.chainchat.store;

import io.vertx.core.json.JsonObject;

/**
 * Created by danigamez on 09/11/2016.
 */
public class UsersStore {

    private JsonObject users = new JsonObject();

    public UsersStore() {}

    public JsonObject get(String username) {
        return users.containsKey(username) ? users.getJsonObject(username) : new JsonObject();
    }
}
