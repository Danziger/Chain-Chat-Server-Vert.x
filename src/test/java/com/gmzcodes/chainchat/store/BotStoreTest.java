package com.gmzcodes.chainchat.store;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.*;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import com.gmzcodes.chainchat.bots.Bot;

/**
 * Created by danigamez on 10/12/2016.
 */
public class BotStoreTest {

    @Before
    public void setUp() {}

    @After
    public void tearDown() {}

    @Test
    public void getNonExistingBotTest() {
        final BotsStore botsStore = new BotsStore();
        final HashMap<String, Bot> bots
                = (HashMap<String, Bot>) Whitebox.getInternalState(botsStore, "bots");

        // PRE:

        assertEquals(0, bots.size());

        // ACTION: Get non-existing bot.

        Bot bot1 = bots.get("bot1");

        // RESULTS:

        assertNull(bot1);

        // POST:

        assertEquals(0, bots.size());
    }

    @Test
    public void putOneBotAndGetItBackTest() {
        final BotsStore botsStore = new BotsStore();
        final HashMap<String, Bot> bots
                = (HashMap<String, Bot>) Whitebox.getInternalState(botsStore, "bots");

        // PRE:

        assertEquals(0, bots.size());

        // ACTION: Put one bot.

        // DON'T USE @Test(expected=Exception.class) in order to check internal state after Exception is thrown!

        Bot bot1 = mock(Bot.class);

        try {
            botsStore.put("bot1", bot1);
        } catch (Exception e) {
            assertTrue(false);
        }

        Bot bot1Back = botsStore.get("bot1");

        // RESULTS:

        assertNotNull(bot1Back);
        assertEquals(bot1, bot1Back);

        // POST:

        assertEquals(1, bots.size());
    }

    @Test
    public void putOneBotAndTryToOverwriteWithSameNameAndBotTest() {
        final BotsStore botsStore = new BotsStore();
        final HashMap<String, Bot> bots
                = (HashMap<String, Bot>) Whitebox.getInternalState(botsStore, "bots");

        // PRE:

        assertEquals(0, bots.size());

        // ACTION: Put one bot.

        // DON'T USE @Test(expected=Exception.class) in order to check internal state after Exception is thrown!

        Bot bot1 = mock(Bot.class);

        try {
            botsStore.put("bot1", bot1);
        } catch (Exception e) {
            assertTrue(false);
        }

        Bot bot1Back = botsStore.get("bot1");

        // RESULTS:

        assertNotNull(bot1Back);
        assertEquals(bot1, bot1Back);

        // POST/PRE:

        assertEquals(1, bots.size());

        // ACTION: Try to overwrite the same bot with the same name.

        // DON'T USE @Test(expected=Exception.class) in order to check internal state after Exception is thrown!

        try {
            botsStore.put("bot1", bot1);
        } catch (Exception e) {
            assertTrue(true);
        }

        // ACTION: Try to get the bot again.

        bot1Back = botsStore.get("bot1");

        assertNotNull(bot1Back);
        assertEquals(bot1, bot1Back);

        // POST:

        assertEquals(1, bots.size());
    }

    @Test
    public void putOneBotAndTryToOverwriteWithSameNameButDifferentBotTest() {
        final BotsStore botsStore = new BotsStore();
        final HashMap<String, Bot> bots
                = (HashMap<String, Bot>) Whitebox.getInternalState(botsStore, "bots");

        // PRE:

        assertEquals(0, bots.size());

        // ACTION: Put one bot.

        // DON'T USE @Test(expected=Exception.class) in order to check internal state after Exception is thrown!

        Bot bot1 = mock(Bot.class);

        try {
            botsStore.put("bot1", bot1);
        } catch (Exception e) {
            assertTrue(false);
        }

        Bot bot1Back = botsStore.get("bot1");

        // RESULTS:

        assertNotNull(bot1Back);
        assertEquals(bot1, bot1Back);

        // POST/PRE:

        assertEquals(1, bots.size());

        // ACTION: Try to overwrite the same bot with the same name.

        // DON'T USE @Test(expected=Exception.class) in order to check internal state after Exception is thrown!

        Bot bot2 = mock(Bot.class);

        try {
            botsStore.put("bot1", bot2);
        } catch (Exception e) {
            assertTrue(true);
        }

        // ACTION: Try to get the bot again.

        bot1Back = botsStore.get("bot1");

        assertNotNull(bot1Back);
        assertEquals(bot1, bot1Back);
        assertNotEquals(bot1Back, bot2);

        // POST:

        assertEquals(1, bots.size());
    }

    @Test
    public void putOneBotTwiceWithDifferentNamesTest() {
        final BotsStore botsStore = new BotsStore();
        final HashMap<String, Bot> bots
                = (HashMap<String, Bot>) Whitebox.getInternalState(botsStore, "bots");

        // PRE:

        assertEquals(0, bots.size());

        // ACTION: Put one bot.

        Bot bot1 = mock(Bot.class);

        // DON'T USE @Test(expected=Exception.class) in order to check internal state after Exception is thrown!

        try {
            botsStore.put("bot1", bot1);
        } catch (Exception e) {
            assertTrue(false);
        }

        // PRE/POST:

        assertEquals(1, bots.size());

        // ACTION: Put same bot again with different name.

        // DON'T USE @Test(expected=Exception.class) in order to check internal state after Exception is thrown!

        try {
            botsStore.put("bot2", bot1);
        } catch (Exception e) {
            assertTrue(true);
        }

        // RESULTS:

        Bot bot1Back = botsStore.get("bot1");
        Bot bot2Back = botsStore.get("bot2");

        assertNotNull(bot1Back);
        assertNotNull(bot2Back);
        assertEquals(bot1, bot1Back);
        assertEquals(bot1, bot2Back);
        assertEquals(bot1Back, bot2Back);

        // POST:

        assertEquals(2, bots.size());
    }

    @Test(expected=Exception.class)
    public void putOneBotAndTryToOverwriteWithSameNameAndBotExceptionTest() throws Exception {
        final BotsStore botsStore = new BotsStore();
        final HashMap<String, Bot> bots
                = (HashMap<String, Bot>) Whitebox.getInternalState(botsStore, "bots");

        // PRE:

        assertEquals(0, bots.size());

        // ACTION: Put one bot.

        Bot bot1 = mock(Bot.class);

        try {
            botsStore.put("bot1", bot1);
        } catch (Exception e) {
            assertTrue(false);
        }

        Bot bot1Back = botsStore.get("bot1");

        // RESULTS:

        assertNotNull(bot1Back);
        assertEquals(bot1, bot1Back);

        // POST/PRE:

        assertEquals(1, bots.size());

        // ACTION: Try to overwrite the same bot with the same name.

        botsStore.put("bot1", bot1); // BOOM!
    }
}
