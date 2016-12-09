package com.gmzcodes.chainchat.contants;

import io.vertx.core.json.JsonObject;

/**
 * Created by danigamez on 08/12/2016.
 */
public final class WebSocketErrorMessagesConstants {
    public static final JsonObject MALFORMED_JSON = new JsonObject()
            .put("type", "error")
            .put("key", "MALFORMED_JSON")
            .put("value", "Malformed JSON.");

    public static final JsonObject MISSING_TOKEN = new JsonObject()
            .put("type", "error")
            .put("key", "MISSING_TOKEN")
            .put("value", "Missing token.");

    public static final JsonObject MISSING_ORIGIN = new JsonObject()
            .put("type", "error")
            .put("key", "MISSING_ORIGIN")
            .put("value", "Missing origin.");

    public static final JsonObject INVALID_TOKEN = new JsonObject()
            .put("type", "error")
            .put("key", "INVALID_TOKEN")
            .put("value", "Invalid token.");

    public static final JsonObject MISSING_DESTINATION = new JsonObject()
            .put("type", "error")
            .put("key", "MISSING_DESTINATION")
            .put("value", "Missing destination.");

    public static final JsonObject INVALID_TIMESTAMP = new JsonObject()
            .put("type", "error")
            .put("key", "INVALID_TIMESTAMP")
            .put("value", "Invalid timestamp.");

    public static final JsonObject INVALID_MESSAGE_TYPE = new JsonObject()
            .put("type", "error")
            .put("key", "INVALID_MESSAGE_TYPE")
            .put("value", "Invalid message type.");

    public static final JsonObject BLOCKED_DESTINATION = new JsonObject()
            .put("type", "error")
            .put("key", "BLOCKED_DESTINATION")
            .put("value", "Blocked destination.");

    public static final JsonObject INVALID_BOT = new JsonObject()
            .put("type", "error")
            .put("key", "INVALID_BOT")
            .put("value", "Invalid bot.");

    public static final JsonObject BLOCKED_BY_BOT = new JsonObject()
            .put("type", "error")
            .put("key", "BLOCKED_BY_BOT")
            .put("value", "Blocked by bot.");

    public static final JsonObject UNKNOWN_DESTINATION = new JsonObject()
            .put("type", "error")
            .put("key", "UNKNOWN_DESTINATION")
            .put("value", "Unknown destination.");

    public static final JsonObject INVALID_DESTINATION = new JsonObject()
            .put("type", "error")
            .put("key", "INVALID_DESTINATION")
            .put("value", "Invalid destination.");
}
