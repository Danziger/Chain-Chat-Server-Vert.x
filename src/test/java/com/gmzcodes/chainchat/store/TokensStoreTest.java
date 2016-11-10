package com.gmzcodes.chainchat.store;

import com.gmzcodes.chainchat.store.TokensStore;
import com.gmzcodes.chainchat.models.Token;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

/**
 * Created by Raul on 10/11/2016.
 */
public class TokensStoreTest {

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    @Test
    public void generateAndVerifyMultipleTokensTest() {
        TokensStore tokensStore = new TokensStore();

        List<String> usedTokenIds = new Stack<>();

        for (int i = 0; i < 32; ++i) {
            String username = UUID.randomUUID().toString();

            Token token = tokensStore.generate(username);

            assertTrue(token.verifyUsername(username)); // Username must match.

            String tokenId = token.getId();

            assertEquals(344, tokenId.length()); // Token ID should be 344 characters.

            assertTrue(!usedTokenIds.contains(tokenId)); // In 32 randomly generated tokens none of them should be repeated!

            usedTokenIds.add(tokenId); // Keep track of used tokens.

            assertEquals(token, tokensStore.generate(username)); // Generating a token for the same user should return the same token!

            assertTrue(tokensStore.verify(tokenId, username)); // Validation OK (:

            assertTrue(!tokensStore.verify(tokenId, "WRONG_USER")); // Validation WRONG, non-matching user
            assertTrue(!tokensStore.verify("WRONG_ID", username)); // Validation WRONG, non-existing ID

            // INTERNAL STATE CHECKS:

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
