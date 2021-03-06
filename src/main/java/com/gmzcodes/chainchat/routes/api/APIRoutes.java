package com.gmzcodes.chainchat.routes.api;

import com.gmzcodes.chainchat.routes.api.auth.AuthAPIRoutes;
import com.gmzcodes.chainchat.routes.api.user.UserAPIRoutes;
import com.gmzcodes.chainchat.store.ConversationsStore;
import com.gmzcodes.chainchat.store.SessionsStore;
import com.gmzcodes.chainchat.store.TokensStore;
import com.gmzcodes.chainchat.store.UsersStore;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.Router;

/**
 * Created by danigamez on 05/10/16.
 */
public final class APIRoutes {

    private APIRoutes() {}

    // TODO: Define constants for mount points!

    /*

    * /user/...
    * /auth/...

    */

    public static Router get(Vertx vertx, AuthProvider authProvider, ConversationsStore conversationsStore, SessionsStore sessionsStore, TokensStore tokensStore, UsersStore usersStore) {
        Router router = Router.router(vertx);

        router.mountSubRouter("/user", UserAPIRoutes.get(vertx));
        router.mountSubRouter("/auth", AuthAPIRoutes.get(vertx, authProvider, conversationsStore, sessionsStore, tokensStore, usersStore));

        return router;
    }
}
