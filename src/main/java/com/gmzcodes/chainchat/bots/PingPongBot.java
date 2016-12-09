package com.gmzcodes.chainchat.bots;

import com.gmzcodes.chainchat.models.Bot;

import io.vertx.core.json.JsonObject;

/**
 * Created by danigamez on 09/12/2016.
 */
public class PingPongBot implements Bot {
    public JsonObject talk(JsonObject message) {
        return null;
    }

    public boolean accepts(String username) {
        return true;
    }
}
