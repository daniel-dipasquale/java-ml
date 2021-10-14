package com.dipasquale.io.serialization.json;

import com.dipasquale.common.factory.ObjectFactory;
import lombok.RequiredArgsConstructor;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

@RequiredArgsConstructor
public final class JsonObjectBuilder {
    private static final Map<JsonObjectType, ObjectFactory<InternalBuilder>> BUILDER_FACTORIES = createBuilderFactories();
    private final Stack<InternalBuilder> builders = new Stack<>();
    private JsonObject jsonObject = null;

    private static Map<JsonObjectType, ObjectFactory<InternalBuilder>> createBuilderFactories() {
        Map<JsonObjectType, ObjectFactory<InternalBuilder>> builderFactories = new EnumMap<>(JsonObjectType.class);

        builderFactories.put(JsonObjectType.OBJECT, ObjectBuilder::new);
        builderFactories.put(JsonObjectType.ARRAY, ArrayBuilder::new);

        return builderFactories;
    }

    public void addObject(final JsonObjectType type) {
        InternalBuilder builder = BUILDER_FACTORIES.get(type).create();

        builders.push(builder);
    }

    public void finalizeObject() {
        JsonObject jsonObjectTemporary = builders.pop().build();

        if (!builders.isEmpty()) {
            builders.peek().addObject(jsonObjectTemporary);
        } else {
            jsonObject = jsonObjectTemporary;
        }
    }

    public void addString(final String value) {
        builders.peek().addString(value);
    }

    public void addNumber(final double value) {
        builders.peek().addNumber(value);
    }

    public void addNumber(final long value) {
        builders.peek().addNumber(value);
    }

    public void addBoolean(final boolean value) {
        builders.peek().addBoolean(value);
    }

    public void addNull() {
        builders.peek().addNull();
    }

    public JsonObject build() {
        return jsonObject.createClone();
    }

    private interface InternalBuilder {
        void addString(String value);

        void addNumber(double value);

        void addNumber(long value);

        void addBoolean(boolean value);

        void addNull();

        void addObject(JsonObject object);

        JsonObject build();
    }

    private static final class ObjectBuilder implements InternalBuilder {
        private final Map<Object, Object> entries = new HashMap<>();
        private Object key = null;

        @Override
        public void addString(final String value) {
            if (key != null) {
                entries.put(key, value);
                key = null;
            } else {
                key = value;
            }
        }

        @Override
        public void addNumber(final double value) {
            entries.put(key, value);
            key = null;
        }

        @Override
        public void addNumber(final long value) {
            entries.put(key, value);
            key = null;
        }

        @Override
        public void addBoolean(final boolean value) {
            entries.put(key, value);
            key = null;
        }

        @Override
        public void addNull() {
            entries.put(key, null);
            key = null;
        }

        @Override
        public void addObject(final JsonObject object) {
            entries.put(key, object);
            key = null;
        }

        @Override
        public JsonObject build() {
            return JsonObject.builder()
                    .type(JsonObjectType.OBJECT)
                    .entries(entries)
                    .build();
        }
    }

    private static final class ArrayBuilder implements InternalBuilder {
        private final Map<Object, Object> entries = new HashMap<>();
        private int length = 0;

        @Override
        public void addString(final String value) {
            entries.put(length++, value);
        }

        @Override
        public void addNumber(final double value) {
            entries.put(length++, value);
        }

        @Override
        public void addNumber(final long value) {
            entries.put(length++, value);
        }

        @Override
        public void addBoolean(final boolean value) {
            entries.put(length++, value);
        }

        @Override
        public void addNull() {
            entries.put(length++, null);
        }

        @Override
        public void addObject(final JsonObject object) {
            entries.put(length++, object);
        }

        @Override
        public JsonObject build() {
            return JsonObject.builder()
                    .type(JsonObjectType.ARRAY)
                    .entries(entries)
                    .build();
        }
    }
}
