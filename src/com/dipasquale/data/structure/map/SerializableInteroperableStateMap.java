package com.dipasquale.data.structure.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractMap;
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
        Map.Entry<String, Object> size = new AbstractMap.SimpleImmutableEntry<>("size", entries.size());

        outputStream.writeObject(size);

        for (Map.Entry<String, Object> entry : entries) {
            outputStream.writeObject(entry);
        }
    }

    public void readFrom(final ObjectInputStream inputStream)
            throws IOException, ClassNotFoundException {
        Map.Entry<String, Object> size = (Map.Entry<String, Object>) inputStream.readObject();

        for (int i = 0, c = (int) size.getValue(); i < c; i++) {
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) inputStream.readObject();

            state.put(entry.getKey(), entry.getValue());
        }
    }
}
