package com.gmzcodes.chainchat.handlers.websocket;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;

/**
 * Created by danigamez on 09/11/2016.
 */
public class WebSocketHandler implements Handler<ServerWebSocket> {

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
