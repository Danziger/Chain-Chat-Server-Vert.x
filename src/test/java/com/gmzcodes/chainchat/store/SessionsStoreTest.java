package com.gmzcodes.chainchat.store;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.impl.ServerWebSocketImpl;

/**
 * Created by danigamez on 09/11/2016.
 */
public class SessionsStoreTest {

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    @Test
    public void getNonExistingTest() {
        final SessionsStore sessionsStore = new SessionsStore();
        final HashMap<String, String> usernameBySessionId
                = (HashMap<String, String>) Whitebox.getInternalState(sessionsStore, "usernameBySessionId");

        // PRE:

        assertEquals(0, usernameBySessionId.size());

        // ACTION: Get non-existing session.

        String username = sessionsStore.getUsername("session1");

        // RESULTS:

        assertNull(username);

        // POST:

        assertEquals(0, usernameBySessionId.size());
    }

    @Test
    public void putOneSessionAndGetItBackTest() {
        final SessionsStore sessionsStore = new SessionsStore();
        final HashMap<String, String> usernameBySessionId
                = (HashMap<String, String>) Whitebox.getInternalState(sessionsStore, "usernameBySessionId");

        // PRE:

        assertEquals(0, usernameBySessionId.size());

        // ACTION: Put 1 session for 1 user.

        try {
            sessionsStore.putSession("session1", "user1");

            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }


        String session1username = sessionsStore.getUsername("session1");

        // RESULTS:

        assertNotNull(session1username);
        assertEquals("user1", session1username);

        // POST:

        assertEquals(1, usernameBySessionId.size());
    }

    @Test
    public void putTwoSessionsForSameUserAndGetThemBackTest() {
        final SessionsStore sessionsStore = new SessionsStore();
        final HashMap<String, String> usernameBySessionId
                = (HashMap<String, String>) Whitebox.getInternalState(sessionsStore, "usernameBySessionId");

        // PRE:

        assertEquals(0, usernameBySessionId.size());

        // ACTION: Put 2 session for same user.

        try {
            sessionsStore.putSession("session1", "user1");
            sessionsStore.putSession("session2", "user1");

            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }

        String session1username = sessionsStore.getUsername("session1");
        String session2username = sessionsStore.getUsername("session2");

        // RESULTS:

        assertNotNull(session1username);
        assertNotNull(session2username);
        assertEquals("user1", session1username);
        assertEquals("user1", session2username);

        // POST:

        assertEquals(2, usernameBySessionId.size());
    }

    @Test
    public void putTwoSessionsForDifferentUsersAndGetThemBackTest() {
        final SessionsStore sessionsStore = new SessionsStore();
        final HashMap<String, String> usernameBySessionId
                = (HashMap<String, String>) Whitebox.getInternalState(sessionsStore, "usernameBySessionId");

        // PRE:

        assertEquals(0, usernameBySessionId.size());

        // ACTION: Put 2 session for 2 different users.

        try {
            sessionsStore.putSession("session1", "user1");
            sessionsStore.putSession("session2", "user2");

            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }

        String session1username = sessionsStore.getUsername("session1");
        String session2username = sessionsStore.getUsername("session2");

        // RESULTS:

        assertNotNull(session1username);
        assertNotNull(session2username);
        assertEquals("user1", session1username);
        assertEquals("user2", session2username);

        // POST:

        assertEquals(2, usernameBySessionId.size());
    }

    @Test
    public void overwriteSessionForSameUserTest() {
        final SessionsStore sessionsStore = new SessionsStore();
        final HashMap<String, String> usernameBySessionId
                = (HashMap<String, String>) Whitebox.getInternalState(sessionsStore, "usernameBySessionId");

        // PRE:

        assertEquals(0, usernameBySessionId.size());

        // ACTION: Put 1 session for 1 user.

        try {
            sessionsStore.putSession("session1", "user1");

            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }

        String session1username = sessionsStore.getUsername("session1");

        // RESULTS:

        assertNotNull(session1username);
        assertEquals("user1", session1username);

        // PRE/POST:

        assertEquals(1, usernameBySessionId.size());

        // ACTION: Try to put that same session for same user.

        // DON'T USE @Test(expected=Exception.class) in order to check internal state after Exception is thrown!

        try {
            sessionsStore.putSession("session1", "user1");

            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        session1username = sessionsStore.getUsername("session1");

        // RESULTS:

        assertNotNull(session1username);
        assertEquals("user1", session1username);
    }

    @Test
    public void overwriteSessionForDifferentUserTest() {
        final SessionsStore sessionsStore = new SessionsStore();
        final HashMap<String, String> usernameBySessionId
                = (HashMap<String, String>) Whitebox.getInternalState(sessionsStore, "usernameBySessionId");

        // PRE:

        assertEquals(0, usernameBySessionId.size());

        // ACTION: Put 1 session for 1 user.

        try {
            sessionsStore.putSession("session1", "user1");

            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }

        String session1username = sessionsStore.getUsername("session1");

        // RESULTS:

        assertNotNull(session1username);
        assertEquals("user1", session1username);

        // PRE/POST:

        assertEquals(1, usernameBySessionId.size());

        // ACTION: Try to put that same session for same user.

        // DON'T USE @Test(expected=Exception.class) in order to check internal state after Exception is thrown!

        try {
            sessionsStore.putSession("session1", "user2");

            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        session1username = sessionsStore.getUsername("session1");

        // RESULTS:

        assertNotNull(session1username);
        assertEquals("user1", session1username);
    }

    @Test(expected=Exception.class)
    public void overwriteSessionForSameUserExceptionTest() throws Exception {
        final SessionsStore sessionsStore = new SessionsStore();
        final HashMap<String, String> usernameBySessionId
                = (HashMap<String, String>) Whitebox.getInternalState(sessionsStore, "usernameBySessionId");

        // PRE:

        assertEquals(0, usernameBySessionId.size());

        // ACTION: Put 1 session for 1 user.

        try {
            sessionsStore.putSession("session1", "user1");

            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }

        String session1username = sessionsStore.getUsername("session1");

        // RESULTS:

        assertNotNull(session1username);
        assertEquals("user1", session1username);

        // PRE/POST:

        assertEquals(1, usernameBySessionId.size());

        // ACTION: Try to put that same session for same user.

        sessionsStore.putSession("session1", "user1");
    }

    @Test(expected=Exception.class)
    public void overwriteSessionForDifferentUserExceptionTest() throws Exception {
        final SessionsStore sessionsStore = new SessionsStore();
        final HashMap<String, String> usernameBySessionId
                = (HashMap<String, String>) Whitebox.getInternalState(sessionsStore, "usernameBySessionId");

        // PRE:

        assertEquals(0, usernameBySessionId.size());

        // ACTION: Put 1 session for 1 user.

        try {
            sessionsStore.putSession("session1", "user1");

            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }

        String session1username = sessionsStore.getUsername("session1");

        // RESULTS:

        assertNotNull(session1username);
        assertEquals("user1", session1username);

        // PRE/POST:

        assertEquals(1, usernameBySessionId.size());

        // ACTION: Try to put that same session for same user.

        sessionsStore.putSession("session1", "user2");
    }
}
