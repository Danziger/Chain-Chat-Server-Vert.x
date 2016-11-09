package com.gmzcodes.chainchat.store;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by danigamez on 09/11/2016.
 */
public class UsersStore {

    private JsonObject users = new JsonObject();

    public UsersStore() {
        users.put("alice", new JsonObject()
                .put("username", "alice")
                .put("name", "Alice Clinton")
                .put("contacts", new JsonArray().add("bob").add("chris"))
                .put("messages", new JsonArray()));

        users.put("bob", new JsonObject()
                .put("username", "bob")
                .put("name", "Bob Trump")
                .put("contacts", new JsonArray().add("alice"))
                .put("messages", new JsonArray()));

        users.put("chris", new JsonObject()
                .put("username", "chris")
                .put("name", "Chris Sambora")
                .put("contacts", new JsonArray().add("chris"))
                .put("messages", new JsonArray()));
    }

    public JsonObject get(String username) {
        return users.containsKey(username) ? users.getJsonObject(username) : new JsonObject();
    }
}
