package com.gmzcodes.chainchat.store;

import java.util.HashMap;
import java.util.Map;

import com.gmzcodes.chainchat.bots.Bot;

/**
 * Created by danigamez on 09/12/2016.
 */
public class BotsStore {

    private final Map<String, Bot> bots = new HashMap<>();

    public Bot get(String botname) {
        return bots.containsKey(botname) ? bots.get(botname) : null;
    }

    public void put(String botname, Bot bot) throws Exception {
        if (bots.containsKey(botname)) {
            throw new Exception("Bot " + botname + " already exists.");
        }

        bots.put(botname, bot);
    }
}
