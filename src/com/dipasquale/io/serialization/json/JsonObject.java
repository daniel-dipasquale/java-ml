package com.dipasquale.io.serialization.json;

import com.dipasquale.io.serialization.SerializableSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public final class JsonObject implements Iterable<Object>, Serializable {
    @Serial
    private static final long serialVersionUID = 7251441417206172642L;
    private static final Map<Class<?>, Converter> CONVERTERS = createConverters();
    private final JsonObjectType type;
    private final Map<Object, Object> entries;

    private static Map<Object, Object> createEntries(final JsonObjectType type, final Map<Object, Object> entries) {
        Map<Object, Object> entriesFixed = switch (type) {
            case ARRAY -> new TreeMap<>(InternalComparator.INSTANCE);

            default -> new HashMap<>();
        };

        if (entries != null) {
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                put(type, entriesFixed, entry.getKey(), entry.getValue());
            }
        }

        return entriesFixed;
    }

    public JsonObject(final JsonObjectType type) {
        this(type, createEntries(type, null));
    }

    @Builder
    private static JsonObject create(final JsonObjectType type, final Map<Object, Object> entries) {
        return new JsonObject(type, createEntries(type, entries));
    }

    private static Map<Class<?>, Converter> createConverters() {
        Map<Class<?>, Converter> converters = new HashMap<>();

        converters.put(Boolean.class, booleanValue -> booleanValue);
        converters.put(Character.class, characterValue -> Character.toString((char) characterValue));
        converters.put(Byte.class, byteValue -> (long) (byte) byteValue);
        converters.put(Short.class, shortValue -> (long) (short) shortValue);
        converters.put(Integer.class, integerValue -> (long) (int) integerValue);
        converters.put(Long.class, longValue -> longValue);
        converters.put(Float.class, floatValue -> (double) (float) floatValue);
        converters.put(Double.class, doubleValue -> doubleValue);
        converters.put(String.class, stringValue -> stringValue);
        converters.put(JsonObject.class, objectValue -> objectValue);

        return converters;
    }

    private static Object sanitizeValue(final Object value) {
        if (value == null) {
            return null;
        }

        Class<?> valueType = value.getClass();
        Converter converter = CONVERTERS.get(valueType);

        if (converter != null) {
            return converter.to(value);
        }

        String message = String.format("unable to convert value of type %s: %s", valueType.getName(), value);

        throw new IllegalArgumentException(message);
    }

    private static long getLength(final Map<Object, Object> entries) {
        Object length = entries.get("length");

        if (!(length instanceof Long)) {
            return 0L;
        }

        return (long) length;
    }

    private static Object put(final JsonObjectType type, final Map<Object, Object> entries, final Object key, final Object value) {
        Object valueFixed = sanitizeValue(value);

        if (type == JsonObjectType.ARRAY && key instanceof Integer) {
            long oldLength = getLength(entries);
            long newLength = (long) (int) key + 1L;

            if (newLength > oldLength) {
                for (int i = (int) oldLength, c = (int) newLength; i < c; i++) {
                    entries.put(i, null);
                }

                entries.put("length", newLength);
            }

            return entries.put(key, valueFixed);
        }

        if (key == null) {
            return entries.put("null", valueFixed);
        }

        return entries.put(key.toString(), valueFixed);
    }

    public JsonObjectType typeof() {
        return type;
    }

    private Object sanitizeKey(final Object key) {
        if (type == JsonObjectType.ARRAY && key instanceof Integer) {
            return key;
        }

        if (key == null) {
            return "null";
        }

        return key.toString();
    }

    public Object get(final Object key) {
        Object keyFixed = sanitizeKey(key);

        return entries.get(keyFixed);
    }

    public Object put(final Object key, final Object value) {
        return put(type, entries, key, value);
    }

    public Object remove(final Object key) {
        if (type == JsonObjectType.ARRAY) {
            if ("length".equals(key)) {
                return getLength(entries);
            }

            if (key instanceof Integer) {
                int oldLength = (int) getLength(entries);
                int newLength = oldLength - 1;
                Object oldValue = entries.get(key);

                for (int i = (int) key; i < newLength; i++) {
                    entries.put(i, entries.get(i + 1));
                }

                entries.remove(newLength);

                return oldValue;
            }
        }

        if (key == null) {
            return entries.remove("null");
        }

        return entries.remove(key.toString());
    }

    @Override
    public Iterator<Object> iterator() {
        if (type == JsonObjectType.OBJECT) {
            return entries.keySet().iterator();
        }

        long length = getLength(entries);

        return entries.keySet().stream()
                .limit(length)
                .iterator();
    }

    public JsonObject createClone() {
        try {
            byte[] bytes = SerializableSupport.serializeObject(this);

            return SerializableSupport.deserializeObject(bytes);
        } catch (IOException | ClassNotFoundException e) {
            throw new UnableToCloneJsonObjectException(e);
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] (%s)", type, entries);
    }

    @FunctionalInterface
    private interface Converter {
        Object to(Object from);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class InternalComparator implements Comparator<Object>, Serializable {
        @Serial
        private static final long serialVersionUID = -4908528203739063677L;
        private static final InternalComparator INSTANCE = new InternalComparator();

        @Serial
        private Object readResolve() {
            return INSTANCE;
        }

        @Override
        public int compare(final Object key1, final Object key2) {
            boolean key1IsString = key1 instanceof String;
            boolean key2IsString = key2 instanceof String;

            if (key1IsString && key2IsString) {
                return ((String) key1).compareTo((String) key2);
            }

            if (key1IsString) {
                return 1;
            }

            if (key2IsString) {
                return -1;
            }

            return Integer.compare((int) key1, (int) key2);
        }
    }
}
