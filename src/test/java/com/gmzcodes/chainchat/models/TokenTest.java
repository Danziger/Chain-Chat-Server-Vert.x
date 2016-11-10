package com.gmzcodes.chainchat.models;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import java.util.List;
import java.util.Stack;
import java.util.UUID;

/**
 * Created by Raul on 10/11/2016.
 */
public class TokenTest {
    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    @Test
    public void getTokenDataBackTest() {
        List<String> usedTokenIds = new Stack<>();

        for (int i = 0; i < 32; ++i) {
            String username = UUID.randomUUID().toString();

            Token token = new Token(username);

            assertTrue(token.verifyUsername(username)); // Username must match.

            String tokenId = token.getId();

            assertEquals(344, tokenId.length()); // Token ID should be 344 characters.

            assertTrue(!usedTokenIds.contains(tokenId)); // In 32 randomly generated tokens none of them should be repeated!

            usedTokenIds.add(tokenId); // Keep track of used tokens.
        }
    }
}
