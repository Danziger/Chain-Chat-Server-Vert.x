package com.gmzcodes.chainchat.store;

import java.nio.file.StandardWatchEventKinds;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import com.gmzcodes.chainchat.models.Conversation;
import com.gmzcodes.chainchat.models.Token;

import io.vertx.core.json.JsonArray;

/**
 * Created by danigamez on 09/11/2016.
 */
public class ConversationsStore {

    private HashMap<String, List<Conversation>> conversationsByUser = new HashMap<String, List<Conversation>>();

    public ConversationsStore() {
        // TODO: This should be mocked!

        Conversation aliceBob = new Conversation("alice", "bob");
        Conversation aliceChris = new Conversation("alice", "chris");

        List aliceConversartions = new Stack();
        aliceConversartions.add(aliceBob);
        aliceConversartions.add(aliceChris);

        List bobConversartions = new Stack();
        bobConversartions.add(aliceBob);

        List chrisConversartions = new Stack();
        chrisConversartions.add(aliceChris);

        conversationsByUser.put("alice", aliceConversartions);
        conversationsByUser.put("bob", bobConversartions);
        conversationsByUser.put("chris", chrisConversartions);
    }

    public List<Conversation> get(String username) {
        return conversationsByUser.get(username);
    }

    public JsonArray getJson(String username) {
        // TODO: Iterate conversationsByUser.get(username) and build JsonArray!

        return new JsonArray();
    }
}
