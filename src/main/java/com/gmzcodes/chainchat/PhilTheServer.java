package com.gmzcodes.chainchat;

import static io.vertx.core.http.HttpHeaders.COOKIE;

import java.text.DateFormat;
import java.time.Instant;
import java.util.Date;

import com.gmzcodes.chainchat.routes.api.APIRoutes;

import com.gmzcodes.chainchat.routes.ws.WebsocketRoutes;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.shiro.ShiroAuth;
import io.vertx.ext.auth.shiro.ShiroAuthOptions;
import io.vertx.ext.auth.shiro.ShiroAuthRealmType;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.handler.sockjs.*;
import io.vertx.ext.web.sstore.LocalSessionStore;

public class PhilTheServer extends AbstractVerticle {

    private final static int PORT = 8080;

    // TODO LIST:

    /*

    - Add Runner util class: https://github.com/vert-x3/vertx-examples/blob/master/web-examples/src/main/java/io/vertx/example/util/Runner.java
    - Crete MongoDB connection and initial data setup method (to be called manually).
    - Create Messages and Users documents.
    - Create client app.
    - Create websocket handler.
    - Añadir algo para manejar multiples logins.

    DUDAS:

    - Login: https://github.com/vert-x3/vertx-examples/blob/master/web-examples/src/main/java/io/vertx/example/web/auth/Server.java
    - TODO: ¿Cómo mockerar eso si se usan librearías? Imposible hacer tests...

     */

    @Override
    public void start(Future<Void> fut) {
        // Create a router object.
        Router router = Router.router(vertx);

        // We need cookies, sessions and request bodies
        router.route().handler(CookieHandler.create());
        router.route().handler(BodyHandler.create());

        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

        // Simple auth service which uses a properties file for user/role info
        AuthProvider authProvider = ShiroAuth.create(vertx, new ShiroAuthOptions()
                .setType(ShiroAuthRealmType.PROPERTIES)
                .setConfig(new JsonObject()
                    .put("properties_path", "src/main/resources/vertx-users.properties")));

        // classpath:

        // TODO: Add favicon!

        // We need a user session handler too to make sure the user is stored in the session between requests
        router.route().handler(UserSessionHandler.create(authProvider));

        // Serve the static private pages from directory "webroot"
        router.route("/static/*").handler(StaticHandler.create("webroot/static").setCachingEnabled(false)); // TODO: Needs auth for images... (split into /private and /public and put in another router class)

        // Public Index Page
        router.get("/").handler(ctx -> {
            ctx.response().sendFile("webroot/index.html");
        });

        router.mountSubRouter("/api", APIRoutes.get(vertx, authProvider));
        // router.mountSubRouter("/static", AssetsRoutes.get(vertx, authProvider));
        // router.mountSubRouter("/ws", WebsocketRoutes.get(vertx, authProvider));

        // Default non-handled requests:
        router.route().handler(ctx -> {
            ctx.fail(404);
        });

        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx
            .createHttpServer()
            .websocketHandler(new Handler<ServerWebSocket>() {
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
            })
            .requestHandler(router::accept)
            .listen(
                    // Retrieve the port from the configuration,
                    // default to 8080.
                    config().getInteger("http.port", 8080),
                    result -> {
                        if (result.succeeded()) {
                            fut.complete();
                        } else {
                            fut.fail(result.cause());
                        }
                    }
            );
    }
}

/*

// TODO: https://github.com/vert-x3/vertx-web/blob/master/vertx-web/src/main/asciidoc/java/index.adoc#sub-routers

// TODO: FUCKING WEBSOCKET ENDPOINT NOT WORKING!

// TODO: SO QUESTION: http://stackoverflow.com/questions/40514637/vert-x-websocket-returning-200-instead-of-101

// TEST 1

BridgeOptions opts = new BridgeOptions()
        .addInboundPermitted(new PermittedOptions().setAddressRegex("*"))
        .addOutboundPermitted(new PermittedOptions().setAddressRegex("*"));

// Create the event bus bridge and add it to the router.
SockJSHandler ebHandler = SockJSHandler.create(vertx).bridge(opts, event -> {
    System.out.println(event.type());

    if (event.type() == BridgeEventType.SOCKET_CREATED) {
        System.out.println("A socket was created");
    } else {
        System.out.println(event.type());
    }

    event.complete(true);
});

router.route("/eventbus/*").handler(ebHandler);

*/

/*

// TEST 2:

SockJSHandler ebHandler = SockJSHandler.create(vertx);

ebHandler.socketHandler(sockJSSocket -> {

    System.out.println("Echo");

    // Just echo the data back
    sockJSSocket.handler(sockJSSocket::write);
});

router.route("/eventbus/*").handler(ebHandler);

*/

/*

// TEST 3:

SockJSHandler ebHandler = SockJSHandler.create(vertx);
BridgeOptions options = new BridgeOptions();

ebHandler.bridge(options, be -> {
    System.out.println("event");

    if (be.type() == BridgeEventType.PUBLISH || be.type() == BridgeEventType.RECEIVE) {
        if (be.getRawMessage().getString("body").equals("armadillos")) {
            // Reject it
            be.complete(false);
            return;
        }
    }

    be.complete(true);
});

router.route("/ws/*").handler(ebHandler);

*/

/*

TEST 4:

// Allow events for the designated addresses in/out of the event bus bridge
BridgeOptions opts = new BridgeOptions()
        .addInboundPermitted(new PermittedOptions().setAddress("chat.to.server"))
        .addOutboundPermitted(new PermittedOptions().setAddress("chat.to.client"));

// Create the event bus bridge and add it to the router.
SockJSHandler ebHandler = SockJSHandler.create(vertx).bridge(opts);
router.route("/eventbus/*").handler(ebHandler);

// Create a router endpoint for the static content.
router.route().handler(StaticHandler.create());

// Start the web server and tell it to use the router to handle requests.
vertx.createHttpServer().requestHandler(router::accept).listen(8080);

EventBus eb = vertx.eventBus();

// Register to listen for messages coming IN to the server
eb.consumer("chat.to.server").handler(message -> {
    // Create a timestamp string
    String timestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date.from(Instant.now()));
    // Send the message back out to all clients with the timestamp prepended.
    eb.publish("chat.to.client", timestamp + ": " + message.body());
});

*/

/*

TEST 5:

// EventBus eb = vertx.eventBus();

eb.addInterceptor(message -> {
    System.out.println("INTERCEPTOR: ");
    System.out.println(message);
});

// Register to listen for messages coming IN to the server
eb.consumer("user2server").handler(message -> {
    // TODO: Check if logged in
    //router.route().handler(UserSessionHandler.create(authProvider));
    System.out.println("CONSUMER");
    System.out.println(message);

    // TODO: Create middleware chain

    // Create a timestamp string
    //String timestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date.from(Instant.now()));

    //Buffer buffer = Buffer.buffer("{\"type\":\"test\"}"));
    //JsonObject msg = new JsonObject(buffer.toString());

    // Send the message back out to all clients with the timestamp prepended.
    eb.publish("server2user", message.body());
});

*/
