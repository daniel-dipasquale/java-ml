package com.dipasquale.ai.rl.neat.core;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.IdentityHashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class SingletonContainer {
    private final Map<Object, Object> values = new IdentityHashMap<>();

    public boolean containsKey(final Object key) {
        return values.containsKey(key);
    }

    public Object getValue(final Object key) {
        return values.get(key);
    }

    public void setValue(final Object key, final Object value) {
        values.put(key, value);
    }
}
