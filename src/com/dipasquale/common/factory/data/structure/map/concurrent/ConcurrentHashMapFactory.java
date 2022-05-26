package com.dipasquale.common.factory.data.structure.map.concurrent;

import com.dipasquale.common.factory.data.structure.map.MapFactory;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public final class ConcurrentHashMapFactory implements MapFactory {
    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    private final int numberOfThreads;

    @Override
    public <TKey, TValue> Map<TKey, TValue> create(final Map<TKey, TValue> other) {
        Map<TKey, TValue> map = new ConcurrentHashMap<>(INITIAL_CAPACITY, LOAD_FACTOR, numberOfThreads);

        if (other != null) {
            map.putAll(other);
        }

        return map;
    }
}
