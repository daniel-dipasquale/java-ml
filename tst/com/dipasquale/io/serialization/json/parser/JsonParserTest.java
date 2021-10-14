package com.dipasquale.io.serialization.json.parser;

import com.dipasquale.io.serialization.json.JsonObject;
import com.dipasquale.io.serialization.json.JsonObjectType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

public final class JsonParserTest {
    private static final JsonParser TEST = new JsonParser();

    @Test
    public void TEST_1()
            throws IOException {
        String json = """
                {
                    "string-name": "string-value",
                    "number-name": 123.23,
                    "boolean-name": false,
                    "object-name": {
                        "object-name-1": "object-value-1"
                    },
                    "array-name": ["1", 2, false]
                }
                """;

        JsonObject result = TEST.parse(json);

        Assertions.assertEquals(JsonObject.builder()
                .type(JsonObjectType.OBJECT)
                .entries(Map.ofEntries(
                        Map.entry("string-name", "string-value"),
                        Map.entry("number-name", 123.23D),
                        Map.entry("boolean-name", false),
                        Map.entry("object-name", JsonObject.builder()
                                .type(JsonObjectType.OBJECT)
                                .entries(Map.ofEntries(
                                        Map.entry("object-name-1", "object-value-1")
                                ))
                                .build()),
                        Map.entry("array-name", JsonObject.builder()
                                .type(JsonObjectType.ARRAY)
                                .entries(Map.ofEntries(
                                        Map.entry(0, "1"),
                                        Map.entry(1, 2L),
                                        Map.entry(2, false),
                                        Map.entry("length", 3)
                                ))
                                .build())
                ))
                .build(), result);
    }
}
