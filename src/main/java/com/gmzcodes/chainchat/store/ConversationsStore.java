package com.gmzcodes.chainchat.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.gmzcodes.chainchat.models.Conversation;

import io.vertx.core.json.JsonObject;

/**
 * Created by danigamez on 09/11/2016.
 */
public class ConversationsStore {

    // TODO: Update to https://commons.apache.org/proper/commons-collections/apidocs/org/apache/commons/collections4/map/MultiKeyMap.html

    private HashMap<String, List<Conversation>> conversationsByUser = new HashMap<>();
    private HashMap<String, Conversation> conversationsById = new HashMap<>();

    public ConversationsStore() {}

    public Conversation get(String... usernames) {
        String roomId = Conversation.buildRoomId(usernames);

        if (conversationsById.containsKey(roomId)) {
            return conversationsById.get(roomId);
        } else {
            Conversation conversation = new Conversation(usernames);

            conversationsById.put(conversation.getRoomId(), conversation);

            for (String username : usernames) {
                if (conversationsByUser.containsKey(username)) {
                    conversationsByUser.get(username).add(conversation);
                } else {
                    List<Conversation> conversations = new ArrayList<>();

                    conversations.add(conversation);
                    conversationsByUser.put(username, conversations);
                }
            }

            return conversation;
        }
    }

    public JsonObject getJson(String username) {
        if (conversationsByUser.containsKey(username)) {
            JsonObject conversationsJson = new JsonObject();

            for (Conversation conversation : conversationsByUser.get(username)) {
                try {
                    conversationsJson.put(conversation.getRoomIdForUser(username), conversation.toJson());
                } catch (Exception e) {
                    // Skip that user...
                }
            }

            return conversationsJson;
        } else {
            return new JsonObject();
        }
    }
}
