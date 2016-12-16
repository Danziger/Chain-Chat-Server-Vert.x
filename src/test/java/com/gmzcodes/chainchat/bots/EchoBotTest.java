package com.gmzcodes.chainchat.bots;

import static junit.framework.TestCase.assertEquals;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.gmzcodes.chainchat.models.Token;

/**
 * Created by danigamez on 15/12/2016.
 */
public class EchoBotTest {
    private final static int MANY_RUNS = 4096;
    private SecureRandom random = new SecureRandom();

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    // TODO: There should be a max message size in general!

    @Test
    public void botEchoNothing() {
        EchoBot echoBot = new EchoBot();

        for (int i = 0; i < MANY_RUNS; ++i) {
            String username = new BigInteger(ThreadLocalRandom.current().nextInt(1, 100), random).toString(32);

            assertEquals("", echoBot.talk(username, ""));
        }
    }

    @Test
    public void botEchoSomething() {
        EchoBot echoBot = new EchoBot();

        for (int i = 0; i < MANY_RUNS; ++i) {
            String username = new BigInteger(ThreadLocalRandom.current().nextInt(1, 100), random).toString(32);
            String message = new BigInteger(ThreadLocalRandom.current().nextInt(1, 100), random).toString(32);

            assertEquals(message, echoBot.talk(username, message));
        }
    }
}
