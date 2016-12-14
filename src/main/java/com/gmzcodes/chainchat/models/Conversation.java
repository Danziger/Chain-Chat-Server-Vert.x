package com.gmzcodes.chainchat.models;

import static java.util.Arrays.asList;

import java.util.*;
import java.util.stream.Collectors;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by danigamez on 10/11/2016.
 */
public class Conversation {

    private final static String SEPARATOR = "::";
    private final static String IDENTIFIER_BASE = "timestamp";
    private final static String IDENTIFIER_FINAL = "id";

    private List<String> usernames = new ArrayList<>();
    private String roomId = "";
    private Map<String, JsonObject> messages = new LinkedHashMap<>();

    public Conversation(String... usernames) {
        this.usernames = Arrays.stream(usernames).distinct().sorted().collect(Collectors.toList());
        this.roomId = buildRoomId(this.usernames);
    }

    private static String buildRoomId(List<String> usernames) {
        return String.join(SEPARATOR, usernames.stream().distinct().sorted().collect(Collectors.toList()));
    }

    public static String buildRoomId(String... usernames) {
        return buildRoomId(asList(usernames));
    }

    public String getRoomId() {
        return this.roomId;
    }

    public String getRoomIdForUser(String username) throws Exception {
        int position = usernames.indexOf(username);

        if (position != -1){
            if (position == 0) {
                return this.roomId.replace(username + SEPARATOR, "");
            } else if (position == usernames.size() - 1) {
                return this.roomId.replace(SEPARATOR + username, "");
            } else {
                return this.roomId.replace(SEPARATOR + username + SEPARATOR, SEPARATOR);
            }
        } else {
            throw new Exception("User " + username + " not part of this conversation.");
        }
    }

    public JsonObject putMessage(JsonObject message) {
        // TODO: States of the message (seen, etc) should be stored per user!

        String baseIdentifier = message.getString("username") + "::" + message.getString(IDENTIFIER_BASE);

        String finalIdentifier = baseIdentifier;
        int index = 0;

        while (messages.containsKey(finalIdentifier)) {
            finalIdentifier = baseIdentifier + "." + (++index);
        }

        // TODO: Messages should be stored with ack from server already!

        message.put(IDENTIFIER_FINAL, finalIdentifier);
        messages.put(finalIdentifier, message);

        return message;
    }

    public JsonArray toJson() {
        JsonArray jsonMessages = new JsonArray();

        messages.values().forEach(jsonMessages::add);

        return jsonMessages;
    }
}
