package com.gmzcodes.chainchat.models;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

/**
 * Created by danigamez on 10/11/2016.
 */
public class Token {
    private final static SecureRandom random = new SecureRandom();

    private String id;
    private String username;
    private Date createdOn;

    public Token(String username) {
        // 3 bytes to 4 chars, 2048 bits = 256 bytes, 4 * 256 bytes / 3 = 341.3 characters + PADDING = 344
        this.id = Base64.getEncoder().encodeToString(new BigInteger(2048, random).toByteArray());
        this.username = username;
        this.createdOn = new Date();
    }

    public String getId() {
        return id;
    }

    public boolean verifyUsername(String username) {
        return this.username.equals(username);
    }
}
