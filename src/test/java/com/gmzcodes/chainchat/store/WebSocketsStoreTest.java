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
 * Created by danigamez on 09/12/2016.
 */
public class WebSocketsStoreTest {

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    @Test
    public void getNonExistingTest() {
        final WebSocketsStore webSocketsStore = new WebSocketsStore();
        final HashMap<String, List<ServerWebSocket>> websocketsByUsername
                = (HashMap<String, List<ServerWebSocket>>) Whitebox.getInternalState(webSocketsStore, "websocketsByUsername");

        // PRE:

        assertEquals(0, websocketsByUsername.size());

        // ACTION: Get non-existing user's WS.

        List<ServerWebSocket> websocketsListUser1 = webSocketsStore.get("user1");

        // RESULTS:

        assertNotNull(websocketsListUser1);
        assertEquals(0, websocketsListUser1.size());

        // POST:

        assertEquals(0, websocketsByUsername.size());
    }

    // TODO: REmove non existing user/WS test

    @Test
    public void putOneUserWithOneWebsocketAndGetItBackTest() {
        final WebSocketsStore webSocketsStore = new WebSocketsStore();
        final HashMap<String, List<ServerWebSocket>> websocketsByUsername
                = (HashMap<String, List<ServerWebSocket>>) Whitebox.getInternalState(webSocketsStore, "websocketsByUsername");

        // PRE:

        assertEquals(0, websocketsByUsername.size());

        // ACTION: Put 1 user with 1 WS.

        ServerWebSocket webSocketMockUser1First = mock(ServerWebSocketImpl.class);

        webSocketsStore.put("user1", webSocketMockUser1First);

        List<ServerWebSocket> websocketsListUser1 = webSocketsStore.get("user1");

        // RESULTS:

        assertNotNull(websocketsListUser1);
        assertEquals(1, websocketsListUser1.size());
        assertEquals(webSocketMockUser1First, websocketsListUser1.get(0));

        // POST:

        assertEquals(1, websocketsByUsername.size());
    }

    @Test
    public void putOneUserWithTwoWebsocketsAndGetThemBackTest() {
        final WebSocketsStore webSocketsStore = new WebSocketsStore();
        final HashMap<String, List<ServerWebSocket>> websocketsByUsername
                = (HashMap<String, List<ServerWebSocket>>) Whitebox.getInternalState(webSocketsStore, "websocketsByUsername");

        // PRE:

        assertEquals(0, websocketsByUsername.size());

        // ACTION: Put 1 user with 2 WSs.

        ServerWebSocket webSocketMockUser1First = mock(ServerWebSocketImpl.class);
        ServerWebSocket webSocketMockUser1Second = mock(ServerWebSocketImpl.class);

        webSocketsStore.put("user1", webSocketMockUser1First);
        webSocketsStore.put("user1", webSocketMockUser1Second);

        List<ServerWebSocket> websocketsListUser1 = webSocketsStore.get("user1");

        // RESULTS:

        assertNotNull(websocketsListUser1);
        assertEquals(2, websocketsListUser1.size());
        assertEquals(webSocketMockUser1First, websocketsListUser1.get(0));
        assertEquals(webSocketMockUser1Second, websocketsListUser1.get(1));

        // POST:

        assertEquals(1, websocketsByUsername.size());
    }

    @Test
    public void putTwoUsersWithOneWebsocketEachAndGetThemBackTest() {
        final WebSocketsStore webSocketsStore = new WebSocketsStore();
        final HashMap<String, List<ServerWebSocket>> websocketsByUsername
                = (HashMap<String, List<ServerWebSocket>>) Whitebox.getInternalState(webSocketsStore, "websocketsByUsername");

        // PRE:

        assertEquals(0, websocketsByUsername.size());

        // ACTION: Put 2 users with 1 WS each.

        ServerWebSocket webSocketMockUser1First = mock(ServerWebSocketImpl.class);
        ServerWebSocket webSocketMockUser2First = mock(ServerWebSocketImpl.class);

        webSocketsStore.put("user1", webSocketMockUser1First);
        webSocketsStore.put("user2", webSocketMockUser2First);

        List<ServerWebSocket> websocketsListUser1 = webSocketsStore.get("user1");
        List<ServerWebSocket> websocketsListUser2 = webSocketsStore.get("user2");

        // RESULTS:

        assertNotNull(websocketsListUser1);
        assertEquals(1, websocketsListUser1.size());
        assertEquals(webSocketMockUser1First, websocketsListUser1.get(0));

        assertNotNull(websocketsListUser2);
        assertEquals(1, websocketsListUser2.size());
        assertEquals(webSocketMockUser2First, websocketsListUser2.get(0));

        // POST:

        assertEquals(2, websocketsByUsername.size());
    }

    @Test
    public void putTwoUsersWithTwoWebsocketsEachGetThemBackAndRemoveThemOneByOneTest() {
        final WebSocketsStore webSocketsStore = new WebSocketsStore();
        final HashMap<String, List<ServerWebSocket>> websocketsByUsername
                = (HashMap<String, List<ServerWebSocket>>) Whitebox.getInternalState(webSocketsStore, "websocketsByUsername");

        // PRE:

        assertEquals(0, websocketsByUsername.size());

        // ACTION: Put 2 users with 2 WSs each.

        ServerWebSocket webSocketMockUser1First = mock(ServerWebSocketImpl.class);
        ServerWebSocket webSocketMockUser1Second = mock(ServerWebSocketImpl.class);
        ServerWebSocket webSocketMockUser2First = mock(ServerWebSocketImpl.class);
        ServerWebSocket webSocketMockUser2Second = mock(ServerWebSocketImpl.class);

        webSocketsStore.put("user1", webSocketMockUser1First);
        webSocketsStore.put("user1", webSocketMockUser1Second);

        webSocketsStore.put("user2", webSocketMockUser2First);
        webSocketsStore.put("user2", webSocketMockUser2Second);

        List<ServerWebSocket> websocketsListUser1 = webSocketsStore.get("user1");
        List<ServerWebSocket> websocketsListUser2 = webSocketsStore.get("user2");

        // RESULTS:

        assertNotNull(websocketsListUser1);
        assertEquals(2, websocketsListUser1.size());
        assertEquals(webSocketMockUser1First, websocketsListUser1.get(0));
        assertEquals(webSocketMockUser1Second, websocketsListUser1.get(1));

        assertNotNull(websocketsListUser2);
        assertEquals(2, websocketsListUser2.size());
        assertEquals(webSocketMockUser2First, websocketsListUser2.get(0));
        assertEquals(webSocketMockUser2Second, websocketsListUser2.get(1));

        // PRE/POST:

        assertEquals(2, websocketsByUsername.size());

        // ACTION: Remove swapped WSs.

        assertFalse(webSocketsStore.remove("user1", webSocketMockUser2First)); // Start
        assertFalse(webSocketsStore.remove("user2", webSocketMockUser1Second)); // End

        websocketsListUser1 = webSocketsStore.get("user1");
        websocketsListUser2 = webSocketsStore.get("user2");

        // RESULTS:

        assertNotNull(websocketsListUser1);
        assertEquals(2, websocketsListUser1.size());
        assertEquals(webSocketMockUser1First, websocketsListUser1.get(0));
        assertEquals(webSocketMockUser1Second, websocketsListUser1.get(1));

        assertNotNull(websocketsListUser2);
        assertEquals(2, websocketsListUser2.size());
        assertEquals(webSocketMockUser2First, websocketsListUser2.get(0));
        assertEquals(webSocketMockUser2Second, websocketsListUser2.get(1));

        // PRE/POST:

        assertEquals(2, websocketsByUsername.size());

        // ACTION: Remove 1 WS from each user.

        assertTrue(webSocketsStore.remove("user1", webSocketMockUser1First)); // Start
        assertTrue(webSocketsStore.remove("user2", webSocketMockUser2Second)); // End

        websocketsListUser1 = webSocketsStore.get("user1");
        websocketsListUser2 = webSocketsStore.get("user2");

        // RESULTS:

        assertNotNull(websocketsListUser1);
        assertEquals(1, websocketsListUser1.size());
        assertEquals(webSocketMockUser1Second, websocketsListUser1.get(0));

        assertNotNull(websocketsListUser2);
        assertEquals(1, websocketsListUser2.size());
        assertEquals(webSocketMockUser2First, websocketsListUser2.get(0));

        // PRE/POST:

        assertEquals(2, websocketsByUsername.size());

        // ACTION: Try to remove already removed WSs.

        assertFalse(webSocketsStore.remove("user1", webSocketMockUser1First));
        assertFalse(webSocketsStore.remove("user2", webSocketMockUser2Second));

        // RESULTS (nothing has changed):

        assertNotNull(websocketsListUser1);
        assertEquals(1, websocketsListUser1.size());
        assertEquals(webSocketMockUser1Second, websocketsListUser1.get(0));

        assertNotNull(websocketsListUser2);
        assertEquals(1, websocketsListUser2.size());
        assertEquals(webSocketMockUser2First, websocketsListUser2.get(0));

        // PRE/POST (nothing has changed):

        assertEquals(2, websocketsByUsername.size());

        // ACTION: Remove 1 WS from user1 (user1 should disappear).

        assertTrue(webSocketsStore.remove("user1", webSocketMockUser1Second));

        websocketsListUser1 = webSocketsStore.get("user1");

        // RESULTS:

        assertNotNull(websocketsListUser1);
        assertEquals(0, websocketsListUser1.size());

        // PRE/POST:

        assertEquals(1, websocketsByUsername.size());

        // ACTION: Remove 1 WS from user2 (user2 should disappear).

        assertTrue(webSocketsStore.remove("user2", webSocketMockUser2First));

        websocketsListUser2 = webSocketsStore.get("user2");

        // RESULTS:

        assertNotNull(websocketsListUser2);
        assertEquals(0, websocketsListUser2.size());

        // POST:

        assertEquals(0, websocketsByUsername.size());
        assertFalse(webSocketsStore.remove("user1", mock(ServerWebSocket.class)));
        assertFalse(webSocketsStore.remove("user2", mock(ServerWebSocket.class)));
    }
}
