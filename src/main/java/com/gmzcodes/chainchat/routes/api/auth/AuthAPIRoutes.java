package com.gmzcodes.chainchat.routes.api.auth;

import com.gmzcodes.chainchat.store.ConversationsStore;
import com.gmzcodes.chainchat.store.SessionsStore;
import com.gmzcodes.chainchat.store.TokensStore;
import com.gmzcodes.chainchat.store.UsersStore;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by danigamez on 05/10/16.
 */
public final class AuthAPIRoutes {

    private AuthAPIRoutes() {}

    // TODO: Define constants for mount points!

    /*

    POST /

    */

    public static Router get(Vertx vertx, AuthProvider authProvider, ConversationsStore conversationsStore, SessionsStore sessionsStore, TokensStore tokensStore, UsersStore usersStore) {
        Router router = Router.router(vertx);

        router.route().consumes("application/json");
        router.route().produces("application/json");

        router.post("/").handler(ctx -> {
            HttpServerResponse res = ctx.response();

            if (isLoggedIn(ctx)) {
                ctx.fail(304); // 304 NOT MODIFIED

                return;
            }

            JsonObject credentials = null;

            try {
                credentials = ctx.getBodyAsJson();
            } catch (DecodeException decodeException) {

            }

            if (credentials == null) {
                ctx.fail(400); // 400 BAD REQUEST

                return;
            }

            final String username = credentials.getString("username");

            // Use the auth handler to perform the authentication for us
            authProvider.authenticate(credentials, login -> {
                if (login.failed()) {
                    ctx.fail(401); // 401 UNAUTHORIZED

                    return;
                }

                ctx.setUser(login.result());

                sessionsStore.put(ctx.session().id(), username);

                JsonObject response = usersStore.get(username);
                response.put("conversations", conversationsStore.getJson(username));
                response.put("token", tokensStore.generate(username).getId());

                // TODO: Return user and all user data (conversarions missing)

                ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json").end(response.toString());
            });
        });

        router.delete("/").handler(ctx -> {
            HttpServerResponse res = ctx.response();

            if (!isLoggedIn(ctx)) {
                ctx.fail(403); // 403 FORBIDDEN

                return;
            }

            ctx.clearUser();

            res.end();
        });

        return router;
    }

    private static boolean isLoggedIn(RoutingContext ctx) {
        return ctx != null && ctx.user() != null;
    }
}
