package com.gmzcodes.chainchat.routes.api.user;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by danigamez on 05/10/16.
 */
public final class UserAPIRoutes {

    private UserAPIRoutes() {}

    // TODO: Define constants for mount points!

    /*

    GET / // TODO
    GET /contacts
    GET /feed

    */

    public static Router get(Vertx vertx) {
        Router router = Router.router(vertx);

        router.route().produces("application/json");

        // Authentication Everywhere!
        router.get("/*").handler(ctx -> {
            if (isLoggedIn(ctx)) ctx.next(); else ctx.fail(403);
        });

        router.get("/contacts").handler(ctx -> {
            ctx.response().end("[]");
        });

        router.get("/feed").handler(ctx -> {
            ctx.response().end("{}");
        });

        return router;
    }

    private static boolean isLoggedIn(RoutingContext ctx) {
        return ctx != null && ctx.user() != null;
    }
}
