package com.gmzcodes.chainchat.handlers.websocket;

import static com.gmzcodes.chainchat.contants.WebSocketErrorMessagesConstants.*;

import com.gmzcodes.chainchat.bots.EchoBot;
import com.gmzcodes.chainchat.bots.PingPongBot;
import com.gmzcodes.chainchat.store.BotsStore;
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
    private BotsStore botsStore;

    // TODO: This could be unit-tested and end-to-end-tested

    public WebSocketHandler(ConversationsStore conversationsStore, TokensStore tokensStore, UsersStore usersStore) {
        this.conversationsStore = conversationsStore;
        this.tokensStore = tokensStore;
        this.usersStore = usersStore;

        BotsStore botsStore = this.botsStore = new BotsStore();

        try {
            botsStore.put("@ping", new PingPongBot());
            botsStore.put("@echo", new EchoBot());
        } catch (Exception duplicatedBotException) {
            // No bots.
        }
    }

    public void handle(final ServerWebSocket ws) {
        if (ws.path().equals("/eventbus")) {

            System.out.println("SOCKET ID = " + ws.binaryHandlerID());

            ws.handler(new Handler<Buffer>() {
                public void handle(Buffer data) {

                    // TODO: Create a parsers chain!

                    // MESSAGE VALIDATIONS:
                    //
                    //  1. MALFORMED_JSON
                    //  2. MISSING_TOKEN        A. missing       B. empty
                    //  3. MISSING_ORIGIN       A. missing       B. empty
                    //  4. MISSING_DESTINATION  A. missing       B. empty
                    //  5. INVALID_TIMESTAMP    A. missing       B. empty         C. invalid format
                    //  6. INVALID_TOKEN        A. non-existing  B. non-matching
                    //  7. BLOCKED_DESTINATION  A. human         B. bot

                    //  8. BOT - INVALID_BOT
                    //  9. BOT - BLOCKED_BY_BOT

                    //  10. HUMAN - UNKNOWN_DESTINATION
                    //  11. HUMAN - INVALID_DESTINATION  A. non-existing destination  B. blocked by destination

                    //  12. INVALID_MESSAGE_TYPE

                    final JsonObject message;

                    try {
                        message = new JsonObject(data.toString());
                    } catch(Exception e) { // 1
                        ws.writeFinalTextFrame(MALFORMED_JSON.toString());

                        return;
                    }

                    final String token = message.containsKey("token") ? message.getString("token") : "";
                    final String from = message.containsKey("username") ? message.getString("username") : "";
                    final String to = message.containsKey("to") ? message.getString("to") : "";
                    final String timestamp = message.containsKey("timestamp") ? message.getString("timestamp") : "";

                    if (token.isEmpty()) { // 2
                        ws.writeFinalTextFrame(MISSING_TOKEN.toString());

                        return;
                    } else if (from.isEmpty()) { // 3
                        ws.writeFinalTextFrame(MISSING_ORIGIN.toString());

                        return;
                    } else if (to.isEmpty()) { // 4
                        ws.writeFinalTextFrame(MISSING_DESTINATION.toString());

                        return;
                    } else if (!timestamp.matches("\\d{4}\\.\\d{2}\\.\\d{2}\\.\\d{2}\\.\\d{2}\\.\\d{2}\\.\\d{3}")) { // 5
                        ws.writeFinalTextFrame(INVALID_TIMESTAMP.toString());

                        return;
                    } else if (!tokensStore.verify(token, from)) { // 6
                        ws.writeFinalTextFrame(INVALID_TOKEN.toString()); // TODO: Maybe create User model  (?)

                        return;
                    } else if (usersStore.get(from).getJsonArray("blacklist").contains(to)) { // 7 // TODO: Create methods to check this things...
                        ws.writeFinalTextFrame(BLOCKED_DESTINATION.toString());

                        return;
                    }

                    final boolean isBot = to.charAt(0) == '@';

                    if (isBot) { // MESSAGE GOES TO A BOT
                        if (botsStore.get(to) == null) { // 8
                            ws.writeFinalTextFrame(INVALID_BOT.toString());

                            return;
                        } else if (botsStore.get(to).accepts(from)) { // 9
                            ws.writeFinalTextFrame(BLOCKED_BY_BOT.toString());

                            return;
                        }
                    } else { // MESSAGE GOES TO A HUMAN
                        if (!usersStore.get(from).getJsonArray("contacts").contains(to)) { // 10
                            ws.writeFinalTextFrame(UNKNOWN_DESTINATION.toString());

                            return;
                        } else if (usersStore.get(to).isEmpty() || usersStore.get(to).getJsonArray("blacklist").contains(from)) { // 11
                            ws.writeFinalTextFrame(INVALID_DESTINATION.toString());

                            return;
                        }
                    }

                    // TODO: Maybe create Message class (?)

                    final JsonObject response = new JsonObject();

                    final String type = message.containsKey("type") ? message.getString("type") : "";

                    switch(type) {
                        case "msg":
                            // TODO: Mock this!

                            // TODO: Store message

                            // TODO: Forward

                            // TODO: Message should be sent to multiple instances of same client!

                            response.put("type", "ack");
                            response.put("value", message.getString("timestamp"));

                            break;

                        case "ack":

                        case "read":

                        default:
                            ws.writeFinalTextFrame(INVALID_MESSAGE_TYPE.toString());

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
