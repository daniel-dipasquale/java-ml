package com.dipasquale.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class SerializableInteroperableStateMap {
    private final Map<String, Object> state = new HashMap<>();

    public <T> T get(final String name) {
        return (T) state.get(name);
    }

    public <T> T getOrDefault(final String name, final T defaultValue) {
        return (T) state.getOrDefault(name, defaultValue);
    }

    public void put(final String name, final Object value) {
        state.put(name, value);
    }

    public void writeTo(final ObjectOutputStream outputStream)
            throws IOException {
        Set<Map.Entry<String, Object>> entries = state.entrySet();

        outputStream.writeObject(entries.size());

        for (Map.Entry<String, Object> entry : entries) {
            outputStream.writeObject(entry.getKey());
            outputStream.writeObject(entry.getValue());
        }
    }

    public void readFrom(final ObjectInputStream inputStream)
            throws IOException, ClassNotFoundException {
        int size = (int) inputStream.readObject();

        for (int i = 0; i < size; i++) {
            String key = (String) inputStream.readObject();

            try {
                state.put(key, inputStream.readObject());
            } catch (Exception e) {
                state.put(key, e);
            }
        }
    }
}
