package com.gmzcodes.chainchat.utils;

import java.util.Map;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;

/**
 * Created by danigamez on 10/11/2016.
 */
public final class JsonAssert {
    private JsonAssert() {}

    public static final void assertJsonEquals(TestContext context, String message, JsonObject expected, JsonObject current, boolean strict) {
        // TODO: Implement messages support.
        // TODO: Implement strict mode.

        for (Map.Entry<String, Object> entry : expected) {
            String key = entry.getKey();
            Object value = entry.getValue();

            context.assertTrue(current.containsKey(key));

            if (value instanceof JsonObject) {
                assertJsonEquals(context, message, (JsonObject)value, current.getJsonObject(key), strict);
            } else if (value instanceof JsonArray) {
                // TODO: context.assertTrue(false);
            } else if (value instanceof String) {
                if (((String) value).charAt(0) == '@') {
                    switch ((String) value) {
                        case "@PRESENT":
                            context.assertTrue(true);
                            break;

                        default:
                            context.assertTrue(false);
                    }
                } else {
                    context.assertEquals(value, current.getString(key));
                }
            } else {
                context.assertTrue(false);
            }
        }
    }

    public static final void assertJsonEquals(TestContext context, JsonObject expected, JsonObject current, boolean strict) {
        assertJsonEquals(context, null, expected, current, strict);
    }
}
