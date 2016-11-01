package com.gmzcodes.chainchat.routes.ws;

import com.gmzcodes.chainchat.routes.api.auth.AuthAPIRoutes;
import com.gmzcodes.chainchat.routes.api.user.UserAPIRoutes;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

/**
 * Created by Raul on 19/10/2016.
 */
public class WebsocketRoutes {
    private WebsocketRoutes() {}



    public static Router get(Vertx vertx, AuthProvider authProvider) {
        Router router = Router.router(vertx);

        System.out.println("WS routes");



        // Allow events for the designated addresses in/out of the event bus bridge
        BridgeOptions opts = new BridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddress("user2server"))
                .addOutboundPermitted(new PermittedOptions().setAddress("server2user"));

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

        router.route("/*").handler(ebHandler);

        EventBus eb = vertx.eventBus();

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





        return router;
    }

    private static boolean isLoggedIn(RoutingContext ctx) {
        return ctx != null && ctx.user() != null;
    }
}
