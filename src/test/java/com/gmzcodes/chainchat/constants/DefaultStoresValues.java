package com.gmzcodes.chainchat.constants;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.powermock.reflect.Whitebox;

import com.gmzcodes.chainchat.bots.Bot;
import com.gmzcodes.chainchat.bots.EchoBot;
import com.gmzcodes.chainchat.bots.PingPongBot;
import com.gmzcodes.chainchat.models.Conversation;
import com.gmzcodes.chainchat.store.*;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by danigamez on 11/12/2016.
 */
public final class DefaultStoresValues {
    private DefaultStoresValues() {}

    public final static BotsStore BOTS_STORE = initializeBotsStore();
    public final static ConversationsStore CONVERSATIONS_STORE = initializeConversationsStore();
    public final static SessionsStore SESSIONS_STORE = initializeSessionsStore();
    public final static TokensStore TOKENS_STORE = initializeTokensStore();
    public final static UsersStore USERS_STORE = initializeUsersStore();
    public final static WebSocketsStore WEB_SOCKETS_STORE = initializeWebSocketsStore();

    private static BotsStore initializeBotsStore() {
        BotsStore botsStore = new BotsStore();

        try {
            botsStore.put("@echobot", new EchoBot());
            botsStore.put("@pingpongbot", new PingPongBot());

            botsStore.put("@blockbot", new Bot() {
                @Override
                public JsonObject talk(JsonObject message) {
                    return new JsonObject();
                }

                @Override
                public boolean accepts(String username) {
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return botsStore;
    }

    private static ConversationsStore initializeConversationsStore() {
        ConversationsStore conversationsStore = new ConversationsStore();

        Conversation aliceBob = new Conversation("alice", "bob");
        Conversation aliceChris = new Conversation("alice", "chris");

        List aliceConversations = new Stack();
        aliceConversations.add(aliceBob);
        aliceConversations.add(aliceChris);

        List bobConversartions = new Stack();
        bobConversartions.add(aliceBob);

        List chrisConversartions = new Stack();
        chrisConversartions.add(aliceChris);

        HashMap<String, List<Conversation>> conversationsByUser = new HashMap<String, List<Conversation>>();

        conversationsByUser.put("alice", aliceConversations);
        conversationsByUser.put("bob", bobConversartions);
        conversationsByUser.put("chris", chrisConversartions);

        Whitebox.setInternalState(conversationsStore, "conversationsByUser", conversationsByUser);

        // TODO: Mock some messages!

        // TODO: Test users that have talked before and some others than haven't!

        return conversationsStore;
    }

    private static SessionsStore initializeSessionsStore() {
        SessionsStore sessionsStore = new SessionsStore();

        return sessionsStore;
    }

    private static TokensStore initializeTokensStore() {
        TokensStore tokensStore = new TokensStore();

        return tokensStore;
    }

    private static UsersStore initializeUsersStore() {
        UsersStore usersStore = new UsersStore();

        JsonObject users = new JsonObject();

        users.put("alice", new JsonObject()
                .put("username", "alice")
                .put("name", "Alice Clinton")
                .put("contacts", new JsonArray().add("bob").add("chris").add("francis"))
                .put("blacklist", new JsonArray().add("dani").add("@blockedbot")));

        users.put("bob", new JsonObject()
                .put("username", "bob")
                .put("name", "Bob Trump")
                .put("contacts", new JsonArray().add("alice"))
                .put("blacklist", new JsonArray()));

        users.put("chris", new JsonObject()
                .put("username", "chris")
                .put("name", "Chris Sambora")
                .put("contacts", new JsonArray().add("bob"))
                .put("blacklist", new JsonArray().add("alice")));

        // TODO: Create methods to create, delete, etc. users (?)

        Whitebox.setInternalState(usersStore, "users", users);

        return usersStore;
    }

    private static WebSocketsStore initializeWebSocketsStore() {
        WebSocketsStore webSocketsStore = new WebSocketsStore();

        return webSocketsStore;
    }
}
