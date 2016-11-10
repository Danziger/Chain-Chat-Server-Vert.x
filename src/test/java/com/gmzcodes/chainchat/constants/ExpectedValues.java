package com.gmzcodes.chainchat.constants;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by danigamez on 10/11/2016.
 */
public final class ExpectedValues {
    private ExpectedValues() {}

    public static final JsonObject USER_ALICE = new JsonObject()
            .put("username", "alice")
            .put("name", "Alice Clinton")
            .put("contacts", new JsonArray().add("bob").add("chris"))
            .put("conversations", new JsonArray())
            .put("token", "@PRESENT");

    public static final JsonObject USER_BOB = new JsonObject()
            .put("username", "bob")
            .put("name", "Bob Trump")
            .put("contacts", new JsonArray().add("alice"))
            .put("conversations", new JsonArray())
            .put("token", "@PRESENT");

    public static final JsonObject USER_CHRIS = new JsonObject()
            .put("username", "chris")
            .put("name", "Chris Sambora")
            .put("contacts", new JsonArray().add("alice"))
            .put("conversations", new JsonArray())
            .put("token", "@PRESENT");
}
