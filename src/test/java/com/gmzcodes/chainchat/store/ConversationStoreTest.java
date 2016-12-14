package com.gmzcodes.chainchat.store;

import static com.gmzcodes.chainchat.utils.JsonAssert.assertJsonEquals;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import com.gmzcodes.chainchat.models.Conversation;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;

/**
 * Created by danigamez on 13/12/2016.
 */
public class ConversationStoreTest {

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    @Test
    public void getNonExistingJSONTest() {
        final ConversationsStore conversationsStore = new ConversationsStore();
        final HashMap<String, List<Conversation>> conversationsByUser
                = (HashMap<String, List<Conversation>>) Whitebox.getInternalState(conversationsStore, "conversationsByUser");
        final HashMap<String, Conversation> conversationsById
                = (HashMap<String, Conversation>) Whitebox.getInternalState(conversationsStore, "conversationsById");

        // PRE:

        assertEquals(0, conversationsByUser.size());
        assertEquals(0, conversationsById.size());

        // ACTION: Get non existing conversation JSON.

        JsonObject emptyJsonObject = conversationsStore.getJson("user1");

        // RESULTS:

        assertNotNull(emptyJsonObject);
        assertEquals(0, emptyJsonObject.size());

        // POST:

        assertEquals(0, conversationsByUser.size());
        assertEquals(0, conversationsById.size());
    }

    @Test
    public void getNonExistingTwoUsersConversationAndCheckJSONTest() {
        final ConversationsStore conversationsStore = new ConversationsStore();
        final HashMap<String, List<Conversation>> conversationsByUser
                = (HashMap<String, List<Conversation>>) Whitebox.getInternalState(conversationsStore, "conversationsByUser");
        final HashMap<String, Conversation> conversationsById
                = (HashMap<String, Conversation>) Whitebox.getInternalState(conversationsStore, "conversationsById");

        // PRE:

        assertEquals(0, conversationsByUser.size());
        assertEquals(0, conversationsById.size());

        // ACTION: Get non existing conversation for user1 and user2

        Conversation conversationUser1User2 = conversationsStore.get("user1", "user2");
        Conversation conversationUser2User1 = conversationsStore.get("user2", "user1");

        JsonObject conversationsJsonUser1 = conversationsStore.getJson("user1");
        JsonObject conversationsJsonUser2 = conversationsStore.getJson("user2");

        // RESULTS:

        assertNotNull(conversationUser1User2);
        assertNotNull(conversationUser2User1);
        assertEquals(conversationUser1User2, conversationUser2User1);
        assertEquals("user1::user2", conversationUser1User2.getRoomId());
        assertEquals("user1::user2", conversationUser2User1.getRoomId());

        JsonObject expectedUser1JSON = new JsonObject()
                .put("user2", new JsonArray());

        JsonObject expectedUser2JSON = new JsonObject()
                .put("user1", new JsonArray());

        assertJsonEquals(expectedUser1JSON, conversationsJsonUser1);
        assertJsonEquals(expectedUser2JSON, conversationsJsonUser2);

        // This next part could be removed, assertJsonEquals is supposed to do that already (TODO: Add strict mode):

        assertNotNull(conversationsJsonUser1);
        assertNotNull(conversationsJsonUser2);
        assertEquals(1, conversationsJsonUser1.size());
        assertEquals(1, conversationsJsonUser2.size());
        assertNotNull(conversationsJsonUser1.getJsonArray("user2"));
        assertNotNull(conversationsJsonUser2.getJsonArray("user1"));
        assertEquals(0, conversationsJsonUser1.getJsonArray("user2").size());
        assertEquals(0, conversationsJsonUser2.getJsonArray("user1").size());

        // POST:

        assertEquals(2, conversationsByUser.size());
        assertEquals(1, conversationsByUser.get("user1").size());
        assertEquals(1, conversationsByUser.get("user2").size());
        assertEquals(conversationUser1User2, conversationsByUser.get("user1").get(0));
        assertEquals(conversationUser2User1, conversationsByUser.get("user2").get(0));

        assertEquals(1, conversationsById.size());
        assertEquals(conversationUser1User2, conversationsById.get("user1::user2"));
    }

    @Test
    public void getNonExistingSelfUsersConversationAndCheckJSONTest() {
        final ConversationsStore conversationsStore = new ConversationsStore();
        final HashMap<String, List<Conversation>> conversationsByUser
                = (HashMap<String, List<Conversation>>) Whitebox.getInternalState(conversationsStore, "conversationsByUser");
        final HashMap<String, Conversation> conversationsById
                = (HashMap<String, Conversation>) Whitebox.getInternalState(conversationsStore, "conversationsById");

        // PRE:

        assertEquals(0, conversationsByUser.size());
        assertEquals(0, conversationsById.size());

        // ACTION: Get non existing conversation for user1 and user2

        Conversation conversationUser1 = conversationsStore.get("user1");

        JsonObject conversationsJsonUser1 = conversationsStore.getJson("user1");

        // RESULTS:

        assertNotNull(conversationUser1);
        assertEquals("user1", conversationUser1.getRoomId());

        assertNotNull(conversationsJsonUser1);
        assertEquals(1, conversationsJsonUser1.size());
        assertNotNull(conversationsJsonUser1.getJsonArray("user1"));
        assertEquals(0, conversationsJsonUser1.getJsonArray("user1").size());

        JsonObject expectedUser1JSON = new JsonObject()
                .put("user1", new JsonArray());

        assertJsonEquals(expectedUser1JSON, conversationsJsonUser1);

        // This next part could be removed, assertJsonEquals is supposed to do that already (TODO: Add strict mode):

        // POST:

        assertEquals(1, conversationsByUser.size());
        assertEquals(1, conversationsByUser.get("user1").size());
        assertEquals(conversationUser1, conversationsByUser.get("user1").get(0));

        assertEquals(1, conversationsById.size());
        assertEquals(conversationUser1, conversationsById.get("user1"));
    }
}
