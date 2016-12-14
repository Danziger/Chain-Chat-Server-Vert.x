package com.gmzcodes.chainchat.constants;

import com.gmzcodes.chainchat.store.BotsStore;

import io.vertx.core.json.JsonObject;

/**
 * Created by danigamez on 14/12/2016.
 */
public final class DummyMessagesValues {
    private DummyMessagesValues() {}

    public final static JsonObject SRC_MESSAGE_ALICE_1 = new JsonObject()
            .put("type", "msg")
            .put("username", "alice")
            .put("to", "bob")
            .put("timestamp", "2016.01.01.12.00.00.000")
            .put("message", "Hi Bob!");

    public final static JsonObject SRC_MESSAGE_ALICE_2 = new JsonObject()
            .put("type", "msg")
            .put("username", "alice")
            .put("to", "bob")
            .put("timestamp", "2016.01.01.12.00.01.000")
            .put("message", "How r u?");

    public final static JsonObject SRC_DUPLICATED_MESSAGE_ALICE = new JsonObject()
            .put("type", "msg")
            .put("username", "alice")
            .put("to", "bob")
            .put("timestamp", "2016.01.01.12.00.03.000")
            .put("message", "Wanna go for a drink tonight?");

    public final static JsonObject FINAL_MESSAGE_ALICE_1 = new JsonObject()
            .put("type", "msg")
            .put("username", "alice")
            .put("to", "bob")
            .put("timestamp", "2016.01.01.12.00.00.000")
            .put("id", "alice::2016.01.01.12.00.00.000")
            .put("message", "Hi Bob!");

    public final static JsonObject FINAL_MESSAGE_ALICE_2 = new JsonObject()
            .put("type", "msg")
            .put("username", "alice")
            .put("to", "bob")
            .put("timestamp", "2016.01.01.12.00.01.000")
            .put("id", "alice::2016.01.01.12.00.01.000")
            .put("message", "How r u?");

    public final static JsonObject FINAL_DUPLICATED_FIRST_MESSAGE_ALICE = new JsonObject()
            .put("type", "msg")
            .put("username", "alice")
            .put("to", "bob")
            .put("timestamp", "2016.01.01.12.00.03.000")
            .put("id", "alice::2016.01.01.12.00.03.000")
            .put("message", "Wanna go for a drink tonight?");

    public final static JsonObject FINAL_DUPLICATED_SECOND_MESSAGE_ALICE = new JsonObject()
            .put("type", "msg")
            .put("username", "alice")
            .put("to", "bob")
            .put("timestamp", "2016.01.01.12.00.03.000")
            .put("id", "alice::2016.01.01.12.00.03.000.1")
            .put("message", "Wanna go for a drink tonight?");

}
