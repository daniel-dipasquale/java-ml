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
                        Map.entry("string_not_empty", "value"),
                        Map.entry("string_empty", ""),
                        Map.entry("number_positive_double", 123.23D),
                        Map.entry("number_zero_double", 0L),
                        Map.entry("number_negative_double", -123.23D),
                        Map.entry("number_positive_long", 123L),
                        Map.entry("number_zero_long", 0L),
                        Map.entry("number_negative_long", -123L),
                        Map.entry("boolean_false", false),
                        Map.entry("boolean_true", true),
                        Map.entry("object_not_empty", JsonObject.builder()
                                .type(JsonObjectType.OBJECT)
                                .entries(Map.ofEntries(
                                        Map.entry("string_not_empty", "value")
                                ))
                                .build()),
                        Map.entry("object_empty", JsonObject.builder()
                                .type(JsonObjectType.OBJECT)
                                .entries(Map.ofEntries())
                                .build()),
                        Map.entry("array_not_empty", JsonObject.builder()
                                .type(JsonObjectType.ARRAY)
                                .entries(Map.ofEntries(
                                        Map.entry(0, "1"),
                                        Map.entry(1, 2L),
                                        Map.entry(2, false),
                                        Map.entry("length", 3)
                                ))
                                .build()),
                        Map.entry("array_empty", JsonObject.builder()
                                .type(JsonObjectType.ARRAY)
                                .entries(Map.ofEntries())
                                .build())
                ))
                .build();

        String result = TEST.toString(jsonObject);
        JsonParser jsonParser = new JsonParser();

        Assertions.assertEquals(jsonObject, jsonParser.parse(result));
    }
}
