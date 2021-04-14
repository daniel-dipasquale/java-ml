package com.dipasquale.data.structure.map;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
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
        Storable size = new Storable("size", entries.size());

        outputStream.writeObject(size);

        for (Map.Entry<String, Object> entry : entries) {
            outputStream.writeObject(new Storable(entry.getKey(), entry.getValue()));
        }
    }

    public void readFrom(final ObjectInputStream inputStream)
            throws IOException, ClassNotFoundException {
        Storable size = (Storable) inputStream.readObject();

        for (int i = 0, c = (int) size.value; i < c; i++) {
            Storable entry = (Storable) inputStream.readObject();

            state.put(entry.key, entry.value);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class Storable implements Serializable {
        @Serial
        private static final long serialVersionUID = 1266337875670000594L;
        private final String key;
        private final Object value;
    }
}
