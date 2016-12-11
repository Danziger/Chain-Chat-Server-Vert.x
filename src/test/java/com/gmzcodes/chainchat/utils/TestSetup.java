package com.gmzcodes.chainchat.utils;

import com.gmzcodes.chainchat.store.*;

/**
 * Created by danigamez on 11/12/2016.
 */
public abstract class TestSetup {
    public abstract void setBotsStore(BotsStore botsStore);
    public abstract void setConversationsStore(ConversationsStore conversationsStore);
    public abstract void setSessionsStore(SessionsStore sessionsStore);
    public abstract void setTokensStore(TokensStore tokensStore);
    public abstract void setUsersStore(UsersStore usersStore);
    public abstract void setWebSocketsStore(WebSocketsStore webSocketsStore);
}
