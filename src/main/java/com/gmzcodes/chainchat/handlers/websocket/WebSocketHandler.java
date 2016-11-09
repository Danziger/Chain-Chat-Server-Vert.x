package com.gmzcodes.chainchat.handlers.websocket;

import com.gmzcodes.chainchat.store.ConversationsStore;
import com.gmzcodes.chainchat.store.TokensStore;
import com.gmzcodes.chainchat.store.UsersStore;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;

/**
 * Created by danigamez on 09/11/2016.
 */
public class WebSocketHandler implements Handler<ServerWebSocket> {

    /*
    - After login, a list of friends, recent messages (read and unread) and token is returned.
      - If token exists, return that. Else, create it. Check for expiration.
    - After socket connected, a list of connected users is kept.

    */

    private ConversationsStore conversationsStore;
    private TokensStore tokensStore;
    private UsersStore usersStore;

    public WebSocketHandler(ConversationsStore conversationsStore, TokensStore tokensStore, UsersStore usersStore) {
        this.conversationsStore = conversationsStore;
        this.tokensStore = tokensStore;
        this.usersStore = usersStore;
    }

    public void handle(final ServerWebSocket ws) {
        if (ws.path().equals("/eventbus")) {
            ws.handler(new Handler<Buffer>() {
                public void handle(Buffer data) {
                    JsonObject message = new JsonObject();

                    try {
                        message = new JsonObject(data.toString());
                    } catch(Exception e) {
                        ws.writeFinalTextFrame("{ \"type\": \"error\", \"value\":\"MALFORMED_JSON\" }");

                        return;
                    }

                    JsonObject response = new JsonObject();

                    String type = message.getString("type");

                    switch(type) {
                        case "ping":
                            response.put("type", "pong");

                            break;

                        default:
                            ws.writeFinalTextFrame("{ \"type\": \"error\", \"value\":\"INVALID MESSAGE TYPE\" }");

                            return;
                    }

                    ws.writeFinalTextFrame(response.toString());
                }
            });
        } else {
            ws.reject();
        }
    }
}
