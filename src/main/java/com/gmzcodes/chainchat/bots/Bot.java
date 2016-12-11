package com.gmzcodes.chainchat.bots;

import io.vertx.core.json.JsonObject;

/**
 * Created by danigamez on 09/12/2016.
 */
public interface Bot {
    public JsonObject talk(JsonObject message);
    public boolean accepts(String username);

    // TODO: ADD help and options method (maybe they are the same?)
}

// TODO: Maybe change for abstract class and give blacklist implementation and some helpers :\