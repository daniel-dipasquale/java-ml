package com.dipasquale.ai.rl.neat.context;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public final class ContextDefaultStateSupport implements Context.StateSupport {
    private final ContextDefault context;

    private void write(final ObjectOutputStream outputStream, final ContextDefaultStateMap state)
            throws IOException {
        Set<Map.Entry<String, Object>> entries = state.entries();
        Map.Entry<String, Object> size = new AbstractMap.SimpleImmutableEntry<>("size", entries.size());

        outputStream.writeObject(size);

        for (Map.Entry<String, Object> entry : entries) {
            outputStream.writeObject(entry);
        }
    }

    @Override
    public void save(final ObjectOutputStream outputStream)
            throws IOException {
        ContextDefaultStateMap state = new ContextDefaultStateMap();

        context.save(state);
        write(outputStream, state);
    }

    private void read(final ObjectInputStream inputStream, final ContextDefaultStateMap state)
            throws IOException, ClassNotFoundException {
        Map.Entry<String, Object> size = (Map.Entry<String, Object>) inputStream.readObject();

        for (int i = 0, c = (int) size.getValue(); i < c; i++) {
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) inputStream.readObject();

            state.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void load(final ObjectInputStream inputStream)
            throws IOException, ClassNotFoundException {
        ContextDefaultStateMap state = new ContextDefaultStateMap();

        read(inputStream, state);
        context.load(state);
    }
}
