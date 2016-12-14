package com.gmzcodes.chainchat.models;

import static com.gmzcodes.chainchat.constants.DummyMessagesValues.*;
import static junit.framework.TestCase.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by danigamez on 13/12/2016.
 */
public class ConversationTest {

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    @Test
    public void conversationOneUserBuildId() {
        assertEquals("user1", Conversation.buildRoomId("user1"));
    }

    @Test(expected=Exception.class)
    public void conversationOneUserUnknownUserException() throws Exception {
        Conversation conversation = new Conversation("user1");

        try {
            assertEquals("user1", conversation.getRoomIdForUser("user1"));
        } catch (Exception e) {
            assertTrue(false);
        }

        conversation.getRoomIdForUser("user");
    }

    @Test
    public void conversationOneUserAndGetIndividualId() {
        Conversation conversation = new Conversation("user1");

        assertNotNull(conversation);
        assertEquals("user1", conversation.getRoomId());

        // Internal state:

        final List<String> usernames
                = (List<String>) Whitebox.getInternalState(conversation, "usernames");

        assertEquals(1, usernames.size());
        assertEquals("user1", usernames.get(0));

        // Individual id:

        try {
            assertEquals("user1", conversation.getRoomIdForUser("user1"));

            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test
    public void conversationTwoSortedUserBuildId() {
        assertEquals("user1::user2", Conversation.buildRoomId("user1", "user2"));
    }

    @Test(expected=Exception.class)
    public void conversationTwoSortedUsersUnknownUserException() throws Exception {
        Conversation conversation = new Conversation("user1", "user2");

        try {
            assertEquals("user2", conversation.getRoomIdForUser("user1"));
            assertEquals("user1", conversation.getRoomIdForUser("user2"));
        } catch (Exception e) {
            assertTrue(false);
        }

        conversation.getRoomIdForUser("user");
    }

    @Test
    public void conversationTwoUsersSortedAndGetIndividualIds() {
        Conversation conversation = new Conversation("user1", "user2");

        assertNotNull(conversation);
        assertEquals("user1::user2", conversation.getRoomId());

        // Internal state:

        final List<String> usernames
                = (List<String>) Whitebox.getInternalState(conversation, "usernames");

        assertEquals(2, usernames.size());
        assertEquals("user1", usernames.get(0));
        assertEquals("user2", usernames.get(1));

        // Individual id:

        try {
            assertEquals("user2", conversation.getRoomIdForUser("user1"));
            assertEquals("user1", conversation.getRoomIdForUser("user2"));

            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test
    public void conversationTwoUnsortedUserBuildId() {
        assertEquals("user1::user2", Conversation.buildRoomId("user2", "user1"));
    }

    @Test(expected=Exception.class)
    public void conversationTwoUnsortedUsersUnknownUserException() throws Exception {
        Conversation conversation = new Conversation("user2", "user1");

        try {
            assertEquals("user2", conversation.getRoomIdForUser("user1"));
            assertEquals("user1", conversation.getRoomIdForUser("user2"));
        } catch (Exception e) {
            assertTrue(false);
        }

        conversation.getRoomIdForUser("user");
    }

    @Test
    public void conversationTwoUsersUnsortedAndGetIndividualIds() {
        Conversation conversation = new Conversation("user2", "user1");

        assertNotNull(conversation);
        assertEquals("user1::user2", conversation.getRoomId());

        // Internal state:

        final List<String> usernames
                = (List<String>) Whitebox.getInternalState(conversation, "usernames");

        assertEquals(2, usernames.size());
        assertEquals("user1", usernames.get(0));
        assertEquals("user2", usernames.get(1));

        // Individual id:

        try {
            assertEquals("user2", conversation.getRoomIdForUser("user1"));
            assertEquals("user1", conversation.getRoomIdForUser("user2"));

            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test
    public void conversationFiveSortedUserBuildId() {
        assertEquals("user1::user2::user3::user4::user5", Conversation.buildRoomId("user1", "user2", "user3", "user4", "user5"));
    }

    @Test(expected=Exception.class)
    public void conversationFiveSortedUsersUnknownUserException() throws Exception {
        Conversation conversation = new Conversation("user1", "user2", "user3", "user4", "user5");

        try {
            assertEquals("user2::user3::user4::user5", conversation.getRoomIdForUser("user1"));
            assertEquals("user1::user3::user4::user5", conversation.getRoomIdForUser("user2"));
            assertEquals("user1::user2::user4::user5", conversation.getRoomIdForUser("user3"));
            assertEquals("user1::user2::user3::user5", conversation.getRoomIdForUser("user4"));
            assertEquals("user1::user2::user3::user4", conversation.getRoomIdForUser("user5"));
        } catch (Exception e) {
            assertTrue(false);
        }

        conversation.getRoomIdForUser("user");
    }

    @Test
    public void conversationFiveUsersSortedAndGetIndividualIds() {
        Conversation conversation = new Conversation("user1", "user2", "user3", "user4", "user5");

        assertNotNull(conversation);
        assertEquals("user1::user2::user3::user4::user5", conversation.getRoomId());

        // Internal state:

        final List<String> usernames
                = (List<String>) Whitebox.getInternalState(conversation, "usernames");

        assertEquals(5, usernames.size());
        assertEquals("user1", usernames.get(0));
        assertEquals("user2", usernames.get(1));
        assertEquals("user3", usernames.get(2));
        assertEquals("user4", usernames.get(3));
        assertEquals("user5", usernames.get(4));

        // Individual id:

        try {
            assertEquals("user2::user3::user4::user5", conversation.getRoomIdForUser("user1"));
            assertEquals("user1::user3::user4::user5", conversation.getRoomIdForUser("user2"));
            assertEquals("user1::user2::user4::user5", conversation.getRoomIdForUser("user3"));
            assertEquals("user1::user2::user3::user5", conversation.getRoomIdForUser("user4"));
            assertEquals("user1::user2::user3::user4", conversation.getRoomIdForUser("user5"));

            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test
    public void conversationFiveUnsortedUserBuildId() {
        assertEquals("user1::user2::user3::user4::user5", Conversation.buildRoomId("user2", "user3", "user5", "user1", "user4"));
    }

    @Test(expected=Exception.class)
    public void conversationFiveUnsortedUsersUnknownUserException() throws Exception {
        Conversation conversation = new Conversation("user5", "user1", "user3", "user4", "user2");

        try {
            assertEquals("user2::user3::user4::user5", conversation.getRoomIdForUser("user1"));
            assertEquals("user1::user3::user4::user5", conversation.getRoomIdForUser("user2"));
            assertEquals("user1::user2::user4::user5", conversation.getRoomIdForUser("user3"));
            assertEquals("user1::user2::user3::user5", conversation.getRoomIdForUser("user4"));
            assertEquals("user1::user2::user3::user4", conversation.getRoomIdForUser("user5"));
        } catch (Exception e) {
            assertTrue(false);
        }

        conversation.getRoomIdForUser("user");
    }

    @Test
    public void conversationFiveUsersUnsortedAndGetIndividualIds() {
        Conversation conversation = new Conversation("user2", "user1", "user4", "user3", "user5");

        assertNotNull(conversation);
        assertEquals("user1::user2::user3::user4::user5", conversation.getRoomId());

        // Internal state:

        final List<String> usernames
                = (List<String>) Whitebox.getInternalState(conversation, "usernames");

        assertEquals(5, usernames.size());
        assertEquals("user1", usernames.get(0));
        assertEquals("user2", usernames.get(1));
        assertEquals("user3", usernames.get(2));
        assertEquals("user4", usernames.get(3));
        assertEquals("user5", usernames.get(4));

        // Individual id:

        try {
            assertEquals("user2::user3::user4::user5", conversation.getRoomIdForUser("user1"));
            assertEquals("user1::user3::user4::user5", conversation.getRoomIdForUser("user2"));
            assertEquals("user1::user2::user4::user5", conversation.getRoomIdForUser("user3"));
            assertEquals("user1::user2::user3::user5", conversation.getRoomIdForUser("user4"));
            assertEquals("user1::user2::user3::user4", conversation.getRoomIdForUser("user5"));

            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test
    public void conversationOneUserBuildIdRepeatedIgnored() {
        assertEquals("user1", Conversation.buildRoomId("user1", "user1"));
    }

    @Test
    public void conversationOneUserAndGetIndividualIdRepeatedIgnored() {
        Conversation conversation = new Conversation("user1", "user1");

        assertNotNull(conversation);
        assertEquals("user1", conversation.getRoomId());

        // Internal state:

        final List<String> usernames
                = (List<String>) Whitebox.getInternalState(conversation, "usernames");

        assertEquals(1, usernames.size());
        assertEquals("user1", usernames.get(0));

        // Individual id:

        try {
            assertEquals("user1", conversation.getRoomIdForUser("user1"));

            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test
    public void conversationTwoSortedUserBuildIdRepeatedIgnored() {
        assertEquals("user1::user2", Conversation.buildRoomId("user1", "user2", "user1"));
    }

    @Test
    public void conversationTwoUsersSortedAndGetIndividualIdsRepeatedIgnored() {
        Conversation conversation = new Conversation("user1", "user2", "user2");

        assertNotNull(conversation);
        assertEquals("user1::user2", conversation.getRoomId());

        // Internal state:

        final List<String> usernames
                = (List<String>) Whitebox.getInternalState(conversation, "usernames");

        assertEquals(2, usernames.size());
        assertEquals("user1", usernames.get(0));
        assertEquals("user2", usernames.get(1));

        // Individual id:

        try {
            assertEquals("user2", conversation.getRoomIdForUser("user1"));
            assertEquals("user1", conversation.getRoomIdForUser("user2"));

            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test
    public void conversationTwoUnsortedUserBuildIdRepeatedIgnored() {
        assertEquals("user1::user2", Conversation.buildRoomId("user2", "user1", "user1"));
    }

    @Test
    public void conversationTwoUsersUnsortedAndGetIndividualIdsRepeatedIgnored() {
        Conversation conversation = new Conversation("user2", "user1", "user2");

        assertNotNull(conversation);
        assertEquals("user1::user2", conversation.getRoomId());

        // Internal state:

        final List<String> usernames
                = (List<String>) Whitebox.getInternalState(conversation, "usernames");

        assertEquals(2, usernames.size());
        assertEquals("user1", usernames.get(0));
        assertEquals("user2", usernames.get(1));

        // Individual id:

        try {
            assertEquals("user2", conversation.getRoomIdForUser("user1"));
            assertEquals("user1", conversation.getRoomIdForUser("user2"));

            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test
    public void conversationFiveSortedUserBuildIdRepeatedIgnored() {
        assertEquals("user1::user2::user3::user4::user5", Conversation.buildRoomId("user1", "user4", "user2", "user3", "user4", "user5"));
    }

    @Test
    public void conversationFiveUsersSortedAndGetIndividualIdsRepeatedIgnored() {
        Conversation conversation = new Conversation("user1", "user2", "user3", "user1", "user4", "user5");

        assertNotNull(conversation);
        assertEquals("user1::user2::user3::user4::user5", conversation.getRoomId());

        // Internal state:

        final List<String> usernames
                = (List<String>) Whitebox.getInternalState(conversation, "usernames");

        assertEquals(5, usernames.size());
        assertEquals("user1", usernames.get(0));
        assertEquals("user2", usernames.get(1));
        assertEquals("user3", usernames.get(2));
        assertEquals("user4", usernames.get(3));
        assertEquals("user5", usernames.get(4));

        // Individual id:

        try {
            assertEquals("user2::user3::user4::user5", conversation.getRoomIdForUser("user1"));
            assertEquals("user1::user3::user4::user5", conversation.getRoomIdForUser("user2"));
            assertEquals("user1::user2::user4::user5", conversation.getRoomIdForUser("user3"));
            assertEquals("user1::user2::user3::user5", conversation.getRoomIdForUser("user4"));
            assertEquals("user1::user2::user3::user4", conversation.getRoomIdForUser("user5"));

            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test
    public void conversationFiveUnsortedUserBuildIdRepeatedIgnored() {
        assertEquals("user1::user2::user3::user4::user5", Conversation.buildRoomId("user2", "user2", "user3", "user5", "user1", "user4"));
    }

    @Test
    public void conversationFiveUsersUnsortedAndGetIndividualIdsRepeatedIgnored() {
        Conversation conversation = new Conversation("user2", "user1", "user4", "user3", "user5", "user5");

        assertNotNull(conversation);
        assertEquals("user1::user2::user3::user4::user5", conversation.getRoomId());

        // Internal state:

        final List<String> usernames
                = (List<String>) Whitebox.getInternalState(conversation, "usernames");

        assertEquals(5, usernames.size());
        assertEquals("user1", usernames.get(0));
        assertEquals("user2", usernames.get(1));
        assertEquals("user3", usernames.get(2));
        assertEquals("user4", usernames.get(3));
        assertEquals("user5", usernames.get(4));

        // Individual id:

        try {
            assertEquals("user2::user3::user4::user5", conversation.getRoomIdForUser("user1"));
            assertEquals("user1::user3::user4::user5", conversation.getRoomIdForUser("user2"));
            assertEquals("user1::user2::user4::user5", conversation.getRoomIdForUser("user3"));
            assertEquals("user1::user2::user3::user5", conversation.getRoomIdForUser("user4"));
            assertEquals("user1::user2::user3::user4", conversation.getRoomIdForUser("user5"));

            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test
    public void conversationGetEmptyJson() {
        Conversation conversation = new Conversation("user1");

        JsonArray messages = conversation.toJson();

        assertNotNull(messages);

        assertEquals(0, messages.size());
    }

    @Test
    public void conversationPutAndGetBackOneMessageJson() {
        Conversation conversation = new Conversation("user1");

        conversation.putMessage(SRC_MESSAGE_ALICE_1);

        JsonArray messages = conversation.toJson();

        assertNotNull(messages);
        assertEquals(1, messages.size());
        assertEquals(FINAL_MESSAGE_ALICE_1, messages.getJsonObject(0));
    }

    @Test
    public void conversationPutAndGetBackTwoMessageJson() {
        Conversation conversation = new Conversation("user1");

        // TODO: Create default fake messages

        conversation.putMessage(SRC_MESSAGE_ALICE_1);
        conversation.putMessage(SRC_MESSAGE_ALICE_2);

        JsonArray messages = conversation.toJson();

        assertNotNull(messages);
        assertEquals(2, messages.size());
        assertEquals(FINAL_MESSAGE_ALICE_1, messages.getJsonObject(0));
        assertEquals(FINAL_MESSAGE_ALICE_2, messages.getJsonObject(1));
    }

    @Test
    public void conversationPutDuplicatedMessageJson() {
        Conversation conversation = new Conversation("user1");

        conversation.putMessage(SRC_DUPLICATED_MESSAGE_ALICE);
        conversation.putMessage(SRC_DUPLICATED_MESSAGE_ALICE.copy());

        JsonArray messages = conversation.toJson();

        assertNotNull(messages);
        assertEquals(2, messages.size());
        assertEquals(FINAL_DUPLICATED_FIRST_MESSAGE_ALICE, messages.getJsonObject(0));
        assertEquals(FINAL_DUPLICATED_SECOND_MESSAGE_ALICE, messages.getJsonObject(1));
    }

    // TODO: User not part of conversation test!
}
