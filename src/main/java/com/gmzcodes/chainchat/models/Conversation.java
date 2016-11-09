package com.gmzcodes.chainchat.models;

/**
 * Created by danigamez on 10/11/2016.
 */
public class Conversation {

    private String userA = "";
    private String userB = "";
    private String roomId = "";

    public Conversation(String userA, String userB) {
        if (userA.compareTo(userB) <= 0) {
            this.userA = userA;
            this.userB = userB;
        } else { // Swap them...
            this.userA = userA;
            this.userB = userB;
        }

        this.roomId = this.userA + "::" + this.userB;
    }
}
