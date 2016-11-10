package com.gmzcodes.chainchat.store;

import com.gmzcodes.chainchat.store.TokensStore;
import com.gmzcodes.chainchat.models.Token;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by Raul on 10/11/2016.
 */
public class TokensStoreTest {

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @Test
    public void generateTest() {
        TokensStore tokens = new TokensStore();
        Token token = tokens.generate("ana");

        assertTrue(token.verifyUsername("ana"));
        assertEquals(205, token.getId().length());
    }

    @Test
    public void verifyTest() {
        TokensStore tokens = new TokensStore();

        assertFalse(tokens.verify("123", "ana"));

        Token token = tokens.generate("ana");
        assertTrue(token.verifyUsername("ana"));
        assertEquals(205, token.getId().length());
    }
}
