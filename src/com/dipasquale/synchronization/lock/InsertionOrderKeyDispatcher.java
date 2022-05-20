package com.dipasquale.synchronization.lock;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

final class InsertionOrderKeyDispatcher<T> implements KeyDispatcher<Integer, T> {
    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    private final Map<IdentityKey, Integer> keys;
    private final AtomicInteger currentKey;
    private final Map<Integer, T> values;

    public InsertionOrderKeyDispatcher(final int numberOfThreads) {
        this.keys = new ConcurrentHashMap<>(INITIAL_CAPACITY, LOAD_FACTOR, numberOfThreads);
        this.currentKey = new AtomicInteger();
        this.values = new ConcurrentHashMap<>(INITIAL_CAPACITY, LOAD_FACTOR, numberOfThreads);
    }

    @Override
    public Integer dispatch(final T value) {
        Integer key = keys.computeIfAbsent(new IdentityKey(value), __ -> currentKey.getAndIncrement());

        values.put(key, value);

        return key;
    }

    @Override
    public T recall(final Integer key) {
        return values.get(key);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class IdentityKey {
        private final Object value;

        @Override
        public int hashCode() {
            return System.identityHashCode(value);
        }

        @Override
        public boolean equals(final Object other) {
            return value == other;
        }
    }
}
