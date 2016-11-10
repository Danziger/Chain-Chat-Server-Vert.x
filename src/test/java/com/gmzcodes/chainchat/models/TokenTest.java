package com.gmzcodes.chainchat.models;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by Raul on 10/11/2016.
 */
public class TokenTest {
    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    @Test
    public void getIdTokenTest() throws Exception{
        Token token = new Token("ana");

        assertEquals(205, token.getId().length());
    }

    @Test
    public void verifyUsernameTest() throws Exception{
        Token token = new Token("ana");

        assertTrue(token.verifyUsername("ana"));
    }
}
