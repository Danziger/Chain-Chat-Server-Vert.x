package com.gmzcodes.chainchat.utils;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;

/**
 * Created by danigamez on 10/11/2016.
 */
public final class JsonAssert {
    private JsonAssert() {
    }

    // TODO: TEST TEST!!

    public static void assertJsonEquals(TestContext context, String message, JsonObject expected, JsonObject current, boolean strict) {
        // TODO: Implement messages support.
        // TODO: Implement strict mode.

        context.assertNotNull(expected);
        context.assertNotNull(current);

        for (Map.Entry<String, Object> entry : expected) {
            String key = entry.getKey();
            Object value = entry.getValue();

            context.assertTrue(current.containsKey(key), current.toString());

            if (value instanceof JsonObject) {
                context.assertTrue(current.getMap().get(key) instanceof JsonObject || current.getMap().get(key) instanceof Map);

                assertJsonEquals(context, message, (JsonObject) value, current.getJsonObject(key), strict);
            } else if (value instanceof JsonArray) {
                context.assertTrue(current.getMap().get(key) instanceof JsonArray || current.getMap().get(key) instanceof List);

                assertJsonEquals(context, message, (JsonArray) value, current.getJsonArray(key), strict);
            } else if (value instanceof String) {
                if (((String) value).charAt(0) == '@') {
                    switch ((String) value) {
                        case "@PRESENT":
                            // TODO: Implement! Not null?
                            context.assertTrue(true);
                            break;

                        default:
                            context.assertTrue(false);
                    }
                } else {
                    assertTrue(current.getMap().get(key) instanceof String);

                    context.assertEquals(value, current.getString(key));
                }
            } else {
                context.assertTrue(false);
            }
        }
    }

    public static void assertJsonEquals(TestContext context, String message, JsonObject expected, JsonObject current) {
        assertJsonEquals(context, message, expected, current, false);
    }

    public static void assertJsonEquals(TestContext context, JsonObject expected, JsonObject current, boolean strict) {
        assertJsonEquals(context, null, expected, current, strict);
    }

    public static void assertJsonEquals(TestContext context, JsonObject expected, JsonObject current) {
        assertJsonEquals(context, expected, current, false);
    }

    public static void assertJsonEquals(TestContext context, String message, JsonArray expected, JsonArray current, boolean strict) {
        // TODO: Implement messages support.
        // TODO: Implement strict mode.

        // TODO: Array expected could also be a JsonObject with two fields: value and other properties such as unsorted: true or other checks

        context.assertNotNull(expected);
        context.assertNotNull(current);

        int index = 0;

        for (Object entry : expected) {
            context.assertFalse(current.hasNull(index));
            context.assertTrue(index < current.size());

            if (entry instanceof JsonObject) {
                context.assertTrue(current.getList().get(index) instanceof JsonObject);

                assertJsonEquals(context, message, (JsonObject) entry, current.getJsonObject(index), strict);
            } else if (entry instanceof JsonArray) {
                context.assertTrue(current.getList().get(index) instanceof JsonArray);

                assertJsonEquals(context, message, (JsonArray) entry, current.getJsonArray(index), strict);
            } else if (entry instanceof String) {
                if (((String) entry).charAt(0) == '@') {
                    switch ((String) entry) {
                        case "@PRESENT":
                            context.assertTrue(true);
                            break;

                        default:
                            context.assertTrue(false);
                    }
                } else {
                    context.assertTrue(current.getList().get(index) instanceof String);

                    context.assertEquals(entry, current.getString(index));
                }
            } else {
                context.assertTrue(false);
            }

            ++index;
        }
    }

    public static void assertJsonEquals(TestContext context, String message, JsonArray expected, JsonArray current) {
        assertJsonEquals(context, message, expected, current, false);
    }

    public static void assertJsonEquals(TestContext context, JsonArray expected, JsonArray current, boolean strict) {
        assertJsonEquals(context, null, expected, current, strict);
    }

    public static void assertJsonEquals(TestContext context, JsonArray expected, JsonArray current) {
        assertJsonEquals(context, expected, current, false);
    }

    // NO CONTEXT

    public static void assertJsonEquals(String message, JsonObject expected, JsonObject current, boolean strict) {
        // TODO: Implement messages support.
        // TODO: Implement strict mode.

        assertNotNull(expected);
        assertNotNull(current);

        for (Map.Entry<String, Object> entry : expected) {
            String key = entry.getKey();
            Object value = entry.getValue();

            assertTrue(current.containsKey(key));

            if (value instanceof JsonObject) {
                assertTrue(current.getMap().get(key) instanceof JsonObject || current.getMap().get(key) instanceof Map);

                assertJsonEquals(message, (JsonObject) value, current.getJsonObject(key), strict);
            } else if (value instanceof JsonArray) {
                assertTrue(current.getMap().get(key) instanceof JsonArray || current.getMap().get(key) instanceof List);

                assertJsonEquals(message, (JsonArray) value, current.getJsonArray(key), strict);
            } else if (value instanceof String) {
                if (((String) value).charAt(0) == '@') {
                    switch ((String) value) {
                        case "@PRESENT":
                            // TODO: Implement! Not null?
                            assertTrue(true);
                            break;

                        default:
                            assertTrue(false);
                    }
                } else {
                    assertTrue(current.getMap().get(key) instanceof String);

                    assertEquals(value, current.getString(key));
                }
            } else {
                assertTrue(false);
            }
        }
    }

    public static void assertJsonEquals(String message, JsonObject expected, JsonObject current) {
        assertJsonEquals(message, expected, current, false);
    }

    public static void assertJsonEquals(JsonObject expected, JsonObject current, boolean strict) {
        assertJsonEquals((String) null, expected, current, strict);
    }

    public static void assertJsonEquals(JsonObject expected, JsonObject current) {
        assertJsonEquals(expected, current, false);
    }

    public static void assertJsonEquals(String message, JsonArray expected, JsonArray current, boolean strict) {
        // TODO: Implement messages support.
        // TODO: Implement strict mode.

        // TODO: Array expected could also be a JsonObject with two fields: value and other properties such as unsorted: true or other checks

        assertNotNull(expected);
        assertNotNull(current);

        int index = 0;

        for (Object entry : expected) {
            assertFalse(current.hasNull(index));
            assertTrue(index < current.size());

            if (entry instanceof JsonObject) {
                assertTrue(current.getList().get(index) instanceof JsonObject);

                assertJsonEquals(message, (JsonObject) entry, current.getJsonObject(index), strict);
            } else if (entry instanceof JsonArray) {
                assertTrue(current.getList().get(index) instanceof JsonArray);

                assertJsonEquals(message, (JsonArray) entry, current.getJsonArray(index), strict);
            } else if (entry instanceof String) {
                if (((String) entry).charAt(0) == '@') {
                    switch ((String) entry) {
                        case "@PRESENT":
                            assertTrue(true);
                            break;

                        default:
                            assertTrue(false);
                    }
                } else {
                    assertTrue(current.getList().get(index) instanceof String);

                    assertEquals(entry, current.getString(index));
                }
            } else {
                assertTrue(false);
            }

            ++index;
        }
    }

    public static void assertJsonEquals(String message, JsonArray expected, JsonArray current) {
        assertJsonEquals(message, expected, current, false);
    }

    public static void assertJsonEquals(JsonArray expected, JsonArray current, boolean strict) {
        assertJsonEquals((String) null, expected, current, strict);
    }

    public static void assertJsonEquals(JsonArray expected, JsonArray current) {
        assertJsonEquals(expected, current, false);
    }
}
