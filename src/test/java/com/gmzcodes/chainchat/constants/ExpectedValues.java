package com.gmzcodes.chainchat.constants;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by danigamez on 10/11/2016.
 */
public final class ExpectedValues {
    private ExpectedValues() {}

    public static final String USERNAME_ALICE = "alice";
    public static final String USERNAME_BOB = "bob";
    public static final String USERNAME_CHRIS = "chris";

    public static final String PASS_ALICE = "alice1234";
    public static final String PASS_BOB = "bob1234";
    public static final String PASS_CHRIS = "chris1234";

    // TODO: Sync with DefaultStoreValues!

    public static final JsonObject USER_ALICE = new JsonObject()
            .put("username", USERNAME_ALICE)
            .put("name", "Alice Clinton")
            .put("contacts", new JsonArray().add("bob").add("chris").add("francis"))
            .put("conversations", new JsonObject())
            .put("token", "@PRESENT");

    public static final JsonObject USER_BOB = new JsonObject()
            .put("username", USERNAME_BOB)
            .put("name", "Bob Trump")
            .put("contacts", new JsonArray().add("alice"))
            .put("conversations", new JsonObject())
            .put("token", "@PRESENT");

    public static final JsonObject USER_CHRIS = new JsonObject()
            .put("username", USERNAME_CHRIS)
            .put("name", "Chris Sambora")
            .put("contacts", new JsonArray().add("alice"))
            .put("conversations", new JsonObject())
            .put("token", "@PRESENT");
}
