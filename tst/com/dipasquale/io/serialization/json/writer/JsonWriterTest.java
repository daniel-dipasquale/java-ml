package com.dipasquale.io.serialization.json.writer;

import com.dipasquale.io.serialization.json.JsonObject;
import com.dipasquale.io.serialization.json.JsonObjectType;
import com.dipasquale.io.serialization.json.parser.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

public final class JsonWriterTest {
    private static final JsonWriter TEST = JsonWriter.getInstance();

    @Test
    public void TEST_1()
            throws IOException {
        JsonObject jsonObject = JsonObject.builder()
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
                .build();

        String result = TEST.toString(jsonObject);
        JsonParser jsonParser = new JsonParser();

        Assertions.assertEquals(jsonObject, jsonParser.parse(result));
    }
}
