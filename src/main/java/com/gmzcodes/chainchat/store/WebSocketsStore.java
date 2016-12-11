package com.gmzcodes.chainchat.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.vertx.core.http.ServerWebSocket;

/**
 * Created by danigamez on 09/11/2016.
 */
public class WebSocketsStore {

    private HashMap<String, List<ServerWebSocket>> websocketsByUsername = new HashMap<>();

    public WebSocketsStore() {}

    public  List<ServerWebSocket> get(String username) {
        return websocketsByUsername.containsKey(username) ? websocketsByUsername.get(username) : new ArrayList<>();
    }

    public void put(String username, ServerWebSocket ws) {
        if (websocketsByUsername.containsKey(username)) {
            websocketsByUsername.get(username).add(ws);
        } else {
            List<ServerWebSocket> wsList = new ArrayList<>();

            wsList.add(ws);

            websocketsByUsername.put(username, wsList);
        }
    }

    public boolean remove(String username, ServerWebSocket ws) {
        if (websocketsByUsername.containsKey(username)) {
            List<ServerWebSocket> websocketsList = websocketsByUsername.get(username);
            boolean removed = websocketsList.remove(ws);

            if (websocketsList.size() == 0) {
                websocketsByUsername.remove(username);

                return true;
            }

            return removed;
        }

        return false;
    }
}
