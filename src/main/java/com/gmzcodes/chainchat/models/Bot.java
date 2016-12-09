package com.gmzcodes.chainchat.models;

import io.vertx.core.json.JsonObject;

/**
 * Created by danigamez on 09/12/2016.
 */
public interface Bot {
    public JsonObject talk(JsonObject message);
    public boolean accepts(String username);
}

// TODO: Maybe change for abstract class and give blacklist implementation and some helpers :\