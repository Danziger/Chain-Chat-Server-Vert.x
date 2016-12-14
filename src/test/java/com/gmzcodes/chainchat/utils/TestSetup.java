package com.gmzcodes.chainchat.utils;

import org.powermock.reflect.Whitebox;

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
    public abstract BotsStore getBotsStore();
    public abstract ConversationsStore getConversationsStore();
    public abstract SessionsStore getSessionsStore();
    public abstract TokensStore getTokensStore();
    public abstract UsersStore getUsersStore();
    public abstract WebSocketsStore getWebSocketsStore();
}
