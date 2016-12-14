package com.gmzcodes.chainchat.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.gmzcodes.chainchat.models.Token;

/**
 * Created by danigamez on 09/11/2016.
 */
public class SessionsStore {

    private HashMap<String, String> usernameBySessionId = new HashMap<>();

    public SessionsStore() {}

    public String getUsername(String sessionId) {
        return usernameBySessionId.containsKey(sessionId) ? usernameBySessionId.get(sessionId) : null;
    }

    public void putSession(String sessionId, String username) throws Exception {
        if (usernameBySessionId.containsKey(sessionId)) {
            throw new Exception("Session with ID = " + sessionId + " already exists.");
        }

        usernameBySessionId.put(sessionId, username);
    }
}
