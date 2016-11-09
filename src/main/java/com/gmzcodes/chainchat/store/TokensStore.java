package com.gmzcodes.chainchat.store;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;

import com.gmzcodes.chainchat.models.Token;

/**
 * Created by danigamez on 09/11/2016.
 */
public class TokensStore {

    private HashMap<String, Token> tokensById = new HashMap<String, Token>();
    private HashMap<String, Token> tokensByUser = new HashMap<String, Token>();

    public TokensStore() {}

    public Token generate(String username) {
        if (tokensByUser.containsKey(username)) {
            return tokensByUser.get(username);
        }

        Token token = new Token(username);

        tokensById.put(token.getId(), token);
        tokensByUser.put(username, token);

        return token;
    }

    public boolean verify(String id, String username) {
        // TODO: Take dates into account!

        if (tokensById.containsKey(id) && tokensById.get(id).verifyUsername(username)) {
            return true;
        }

        return false;
    }
}
