package com.gmzcodes.chainchat.handlers.websocket;

import static com.gmzcodes.chainchat.contants.WebSocketErrorMessagesConstants.*;
import static io.vertx.core.http.HttpHeaders.COOKIE;

import com.gmzcodes.chainchat.store.*;

import io.vertx.core.Handler;
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

    private BotsStore botsStore;
    private ConversationsStore conversationsStore;
    private SessionsStore sessionsStore;
    private TokensStore tokensStore;
    private UsersStore usersStore;
    private WebSocketsStore webSocketsStore;

    // TODO: This could be unit-tested and end-to-end-tested

    public WebSocketHandler(BotsStore botsStore,
                            ConversationsStore conversationsStore,
                            SessionsStore sessionsStore,
                            TokensStore tokensStore,
                            UsersStore usersStore,
                            WebSocketsStore webSocketsStore
    ) {
        this.botsStore = botsStore;
        this.conversationsStore = conversationsStore;
        this.sessionsStore = sessionsStore;
        this.tokensStore = tokensStore;
        this.usersStore = usersStore;
        this.webSocketsStore = webSocketsStore;
    }

    public void handle(final ServerWebSocket ws) {
        if (ws.path().equals("/eventbus")) {
            String username = sessionsStore.getUsername(ws.headers().get(COOKIE).split("session=")[1].split(";")[0]);

            webSocketsStore.put(username, ws);

            ws.handler(data -> {
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

                JsonObject message;

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

                // TODO: Check that "to" matches username

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

                // TODO: We should store the GET of the bot or user for later!

                if (isBot) { // MESSAGE GOES TO A BOT
                    if (botsStore.get(to) == null) { // 8
                        ws.writeFinalTextFrame(INVALID_BOT.toString());

                        return;
                    } else if (!botsStore.get(to).accepts(from)) { // 9
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

                final JsonObject response;

                final String type = message.containsKey("type") ? message.getString("type") : "";

                message.remove("token"); // TODO: Test this!!

                switch(type) {
                    case "msg":
                        // TODO: Test with conversations in store (add that test data).

                        // TODO: Test offline user messages go to the store.

                        // TODO: add server ack in message

                        // TODO: Send ACK_SERVER

                        // TODO: Replay message to other instances (including ACK)

                        // TODO: Store message (including ACK)

                        // TODO: Forward to destination

                        // TODO: Should send to multiple to's (group or multiple instances) and replay to multiple from's!

                        // response.put("type", "ack");
                        // response.put("value", message.getString("timestamp"));

                        message = conversationsStore.get(from, to).putMessage(message);

                        if (isBot) {
                            // ACK ACK should go in same message!

                            response = botsStore.get(to).talk(message);

                        } else {
                            response = new JsonObject()
                                    .put("type", "stored")
                                    .put("value", message.getString("id"));
                        }

                        break;

                    case "ack":

                    case "read":

                    default:
                        ws.writeFinalTextFrame(INVALID_MESSAGE_TYPE.toString());

                        return;
                }

                ws.writeFinalTextFrame(response.toString());
            });

            ws.closeHandler(done -> webSocketsStore.remove(username, ws));
        } else {
            ws.reject();
        }
    }
}
