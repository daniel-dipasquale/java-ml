package com.dipasquale.io.serialization.json.writer;

import com.dipasquale.io.serialization.json.JsonObject;
import com.dipasquale.io.serialization.json.JsonObjectType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonWriter {
    private static final Map<Class<?>, JsonValueType> JSON_VALUE_TYPES = createJsonValueTypes();
    private static final Map<Character, String> ENCODED_ESCAPED_CHARACTERS = createEncodedEscapedCharacters();
    private static final JsonWriter INSTANCE = new JsonWriter();

    private static Map<Class<?>, JsonValueType> createJsonValueTypes() {
        Map<Class<?>, JsonValueType> converters = new HashMap<>();

        converters.put(Boolean.class, JsonValueType.BOOLEAN);
        converters.put(Long.class, JsonValueType.INTEGER);
        converters.put(Double.class, JsonValueType.FLOAT);
        converters.put(String.class, JsonValueType.STRING);
        converters.put(JsonObject.class, JsonValueType.OBJECT);

        return converters;
    }

    private static Map<Character, String> createEncodedEscapedCharacters() {
        Map<Character, String> encodedEscapedCharacters = new HashMap<>();

        encodedEscapedCharacters.put('\b', "\\b");
        encodedEscapedCharacters.put('\f', "\\f");
        encodedEscapedCharacters.put('\n', "\\n");
        encodedEscapedCharacters.put('\r', "\\r");
        encodedEscapedCharacters.put('\t', "\\t");
        encodedEscapedCharacters.put('"', "\\\"");
        encodedEscapedCharacters.put('\\', "\\\\");

        return encodedEscapedCharacters;
    }

    public static JsonWriter getInstance() {
        return INSTANCE;
    }

    private void writeStartIfUniterated(final Writer writer, final IterationTracker iterationTracker)
            throws IOException {
        if (iterationTracker.keysIterated > 0) {
            return;
        }

        if (iterationTracker.jsonObject.typeof() == JsonObjectType.ARRAY) {
            writer.append('[');
        } else {
            writer.append('{');
        }
    }

    private static JsonValueType getType(final Object value) {
        if (value == null) {
            return JsonValueType.NULL;
        }

        return JSON_VALUE_TYPES.get(value.getClass());
    }

    private void writeString(final Writer writer, final String value)
            throws IOException {
        writer.append('"');

        for (int i = 0, c = value.length(); i < c; i++) {
            char character = value.charAt(i);
            String escapedCharacter = ENCODED_ESCAPED_CHARACTERS.get(character);

            if (escapedCharacter != null) {
                writer.append(escapedCharacter);
            } else {
                writer.append(character);
            }
        }

        writer.append('"');
    }

    private void writeKeyIfObject(final Writer writer, final JsonObjectType type, final Object key)
            throws IOException {
        if (type == JsonObjectType.OBJECT) {
            writeString(writer, key.toString());
            writer.append(':');
        }
    }

    private void writeNull(final Writer writer, final JsonObjectType type, final Object key)
            throws IOException {
        writeKeyIfObject(writer, type, key);
        writer.append("null");
    }

    private void writeBoolean(final Writer writer, final JsonObjectType type, final Object key, final boolean value)
            throws IOException {
        writeKeyIfObject(writer, type, key);
        writer.append(Boolean.toString(value));
    }

    private void writeLong(final Writer writer, final JsonObjectType type, final Object key, final long value)
            throws IOException {
        writeKeyIfObject(writer, type, key);
        writer.append(Long.toString(value));
    }

    private static String format(final double value) {
        if (Double.compare(value, Math.floor(value)) != 0) {
            return Double.toString(value);
        }

        return Long.toString((long) value);
    }

    private void writeDouble(final Writer writer, final JsonObjectType type, final Object key, final double value)
            throws IOException {
        writeKeyIfObject(writer, type, key);
        writer.append(format(value));
    }

    private void writeString(final Writer writer, final JsonObjectType type, final Object key, final String value)
            throws IOException {
        writeKeyIfObject(writer, type, key);
        writeString(writer, value);
    }

    private void writeEnd(final Writer writer, final JsonObjectType type)
            throws IOException {
        if (type == JsonObjectType.ARRAY) {
            writer.append(']');
        } else {
            writer.append('}');
        }
    }

    private void write(final Writer writer, final Stack<IterationTracker> iterationTrackers)
            throws IOException {
        while (!iterationTrackers.isEmpty()) {
            int startingSize = iterationTrackers.size();
            IterationTracker iterationTracker = iterationTrackers.peek();

            writeStartIfUniterated(writer, iterationTracker);

            while (iterationTracker.keys.hasNext() && startingSize == iterationTrackers.size()) {
                Object key = iterationTracker.keys.next();
                Object value = iterationTracker.jsonObject.get(key);
                JsonValueType valueType = getType(value);

                if (iterationTracker.keysIterated++ > 0) {
                    writer.append(',');
                }

                switch (valueType) {
                    case NULL -> writeNull(writer, iterationTracker.jsonObject.typeof(), key);

                    case BOOLEAN -> writeBoolean(writer, iterationTracker.jsonObject.typeof(), key, (boolean) value);

                    case INTEGER -> writeLong(writer, iterationTracker.jsonObject.typeof(), key, (long) value);

                    case FLOAT -> writeDouble(writer, iterationTracker.jsonObject.typeof(), key, (double) value);

                    case STRING -> writeString(writer, iterationTracker.jsonObject.typeof(), key, (String) value);

                    case OBJECT -> {
                        writeKeyIfObject(writer, iterationTracker.jsonObject.typeof(), key);
                        iterationTrackers.push(new IterationTracker((JsonObject) value));
                    }
                }
            }

            if (startingSize == iterationTrackers.size()) {
                iterationTrackers.pop();
                writeEnd(writer, iterationTracker.jsonObject.typeof());
            }
        }
    }

    public void write(final Writer writer, final JsonObject jsonObject)
            throws IOException {
        Stack<IterationTracker> iterationTrackers = new Stack<>();

        iterationTrackers.push(new IterationTracker(jsonObject));
        write(writer, iterationTrackers);
    }

    public String toString(final JsonObject jsonObject)
            throws IOException {
        try (StringWriter stringWriter = new StringWriter()) {
            write(stringWriter, jsonObject);

            return stringWriter.toString();
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class IterationTracker {
        private final JsonObject jsonObject;
        private final Iterator<Object> keys;
        private int keysIterated = 0;

        private IterationTracker(final JsonObject jsonObject) {
            this(jsonObject, jsonObject.iterator());
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private enum JsonValueType {
        NULL,
        BOOLEAN,
        INTEGER,
        FLOAT,
        STRING,
        OBJECT
    }
}
