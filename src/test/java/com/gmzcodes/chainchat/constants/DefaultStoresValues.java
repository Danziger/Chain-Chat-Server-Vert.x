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

    public static BotsStore BOTS_STORE() {
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

    public static ConversationsStore CONVERSATIONS_STORE() {
        ConversationsStore conversationsStore = new ConversationsStore();

        return conversationsStore;
    }

    public static SessionsStore SESSIONS_STORE() {
        SessionsStore sessionsStore = new SessionsStore();

        return sessionsStore;
    }

    public static TokensStore TOKENS_STORE() {
        TokensStore tokensStore = new TokensStore();

        return tokensStore;
    }

    public static UsersStore USERS_STORE() {
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

    public static WebSocketsStore WEB_SOCKETS_STORE() {
        WebSocketsStore webSocketsStore = new WebSocketsStore();

        return webSocketsStore;
    }
}
