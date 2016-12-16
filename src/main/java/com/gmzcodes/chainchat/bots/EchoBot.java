package com.gmzcodes.chainchat.bots;

import io.vertx.core.json.JsonObject;

/**
 * Created by danigamez on 09/12/2016.
 */
public class EchoBot implements Bot {
    public String talk(String username, String message) {
        return message;
    }

    public boolean accepts(String username) {
        return true;
    }
}
