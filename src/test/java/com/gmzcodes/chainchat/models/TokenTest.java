package com.gmzcodes.chainchat.models;


import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import java.util.List;
import java.util.Stack;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

/**
 * Created by Raul on 10/11/2016.
 */
public class TokenTest {

    private final static int EXPECTED_TOKEN_LENGTH = 344;
    private final static int MANY_RUNS = 4096;

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    @Test
    public void tokenLengthTest() {
        assertEquals(EXPECTED_TOKEN_LENGTH, (int) Whitebox.getInternalState(Token.class, "EXPECTED_TOKEN_LENGTH"));
    }

    @Test
    public void getTokenDataBackTest() {
        List<String> usedTokenIds = new Stack<>();

        // PRE:

        assertEquals(EXPECTED_TOKEN_LENGTH, (int) Whitebox.getInternalState(Token.class, "EXPECTED_TOKEN_LENGTH"));

        // SOME ids could be 340 characters long, test should be executed this many times in order to be certain enough
        // this has been implemented right!

        for (int i = 0; i < MANY_RUNS; ++i) {
            String username = UUID.randomUUID().toString();

            Token token = new Token(username);

            assertTrue(token.verifyUsername(username)); // Username must match.

            String tokenId = token.getId();

            assertEquals(EXPECTED_TOKEN_LENGTH, tokenId.length()); // Token ID should be EXPECTED_TOKEN_LENGTH characters.

            assertTrue(!usedTokenIds.contains(tokenId)); // In 32 randomly generated tokens none of them should be repeated!

            usedTokenIds.add(tokenId); // Keep track of used tokens.
        }
    }
}
