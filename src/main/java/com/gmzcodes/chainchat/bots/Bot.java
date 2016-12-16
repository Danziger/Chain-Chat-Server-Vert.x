package com.gmzcodes.chainchat.bots;

import io.vertx.core.json.JsonObject;

/**
 * Created by danigamez on 09/12/2016.
 */
public interface Bot {
    public String talk(String username, String message);
    public boolean accepts(String username);

    // TODO: There should be a wrapper around the user-defined bot class with validatind and checks for security and privacy!

    // TODO: ADD help and options method (maybe they are the same?)
}

// TODO: Maybe change for abstract class and give blacklist implementation and some helpers :\