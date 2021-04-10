package com.dipasquale.ai.rl.neat.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final class ContextDefaultStateMap {
    private final Map<String, Object> state = new HashMap<>();

    public <T> T get(final String name) {
        return (T) state.get(name);
    }

    public void put(final String name, final Object value) {
        state.put(name, value);
    }

    public Set<Map.Entry<String, Object>> entries() {
        return state.entrySet();
    }
}
