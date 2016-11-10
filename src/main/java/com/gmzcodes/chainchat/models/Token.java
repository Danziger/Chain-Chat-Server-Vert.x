package com.gmzcodes.chainchat.models;

import java.math.BigInteger;
import java.security.SecureRandom;
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
        this.id = new BigInteger(1024, random).toString(32);
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
