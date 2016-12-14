package com.gmzcodes.chainchat.store;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.gmzcodes.chainchat.models.Token;

/**
 * Created by Raul on 10/11/2016.
 */
public class TokensStoreTest {

    private final static int EXPECTED_TOKEN_LENGTH = 344;
    private final static int MANY_RUNS = 4096;

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    @Test
    public void generateAndVerifyMultipleTokensTest() {
        TokensStore tokensStore = new TokensStore();

        List<String> usedTokenIds = new Stack<>();

        // PRE (this check doesn't belong to this test, but anyway it's better to check to avoid confusions if we find an error):

        assertEquals(EXPECTED_TOKEN_LENGTH, (int) Whitebox.getInternalState(Token.class, "EXPECTED_TOKEN_LENGTH"));

        // SOME ids could be 340 characters long, test should be executed this many times in order to be certain enough
        // this has been implemented right!

        for (int i = 0; i < MANY_RUNS; ++i) {
            String username = UUID.randomUUID().toString();

            Token token = tokensStore.generate(username);

            assertTrue(token.verifyUsername(username)); // Username must match.

            String tokenId = token.getId();

            assertEquals(EXPECTED_TOKEN_LENGTH, tokenId.length()); // Token ID should be 344 characters.

            assertTrue(!usedTokenIds.contains(tokenId)); // In 32 randomly generated tokens none of them should be repeated!

            usedTokenIds.add(tokenId); // Keep track of used tokens.

            assertEquals(token, tokensStore.generate(username)); // Generating a token for the same user should return the same token!

            assertTrue(tokensStore.verify(tokenId, username)); // Validation OK (:

            assertTrue(!tokensStore.verify(tokenId, "WRONG_USER")); // Validation WRONG, non-matching user
            assertTrue(!tokensStore.verify("WRONG_ID", username)); // Validation WRONG, non-existing ID

            // INTERNAL STATE CHECKS:

            // TODO: Update to use WhiteBox

            try {
                Field tokensByIdField = tokensStore.getClass().getDeclaredField("tokensById");
                Field tokensByUserField = tokensStore.getClass().getDeclaredField("tokensByUser");

                tokensByIdField.setAccessible(true);
                tokensByUserField.setAccessible(true);

                HashMap<String, Token> tokensById = (HashMap<String, Token>)tokensByIdField.get(tokensStore);
                HashMap<String, Token> tokensByUser = (HashMap<String, Token>)tokensByUserField.get(tokensStore);

                assertTrue(tokensById.containsKey(tokenId));
                assertTrue(tokensByUser.containsKey(username));

                assertEquals(token, tokensById.get(tokenId));
                assertEquals(token, tokensByUser.get(username));
            } catch(Exception e) {
                assertTrue(false);
            }
        }
    }
}
