package com.dipasquale.data.structure.map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public final class SortedByValueRankedAggregator<TKey, TValue extends Comparable<TValue>> {
    private final Comparator<TValue> comparator;
    private final Factory<TKey, TValue> mapFactory;
    private SortedByValueMap<TKey, TValue> map;
    @Getter
    private Set<TKey> keys;
    private final TValue initialExtremeValue;
    private final AtomicReference<TValue> extremeValue;
    private final int limit;
    private final AtomicInteger size;
    private final ReadWriteLock lock;

    private SortedByValueRankedAggregator(final Comparator<TValue> comparator, final Factory<TKey, TValue> mapFactory, final TValue initialExtremeValue, final int limit) {
        SortedByValueMap<TKey, TValue> map = mapFactory.create(comparator);

        this.comparator = comparator;
        this.mapFactory = mapFactory;
        this.map = map;
        this.keys = Collections.unmodifiableSet(map.keySet());
        this.initialExtremeValue = initialExtremeValue;
        this.extremeValue = new AtomicReference<>(initialExtremeValue);
        this.limit = limit;
        this.size = new AtomicInteger();
        this.lock = new ReentrantReadWriteLock();
    }

    public static <T> SortedByValueRankedAggregator<T, Long> createHighestRankedConcurrent(final int limit) {
        Comparator<Long> comparator = Long::compare;
        Factory<T, Long> mapFactory = SortedByValueMap::createHashConcurrent;
        long initialExtremeValue = 0L;

        return new SortedByValueRankedAggregator<>(comparator, mapFactory, initialExtremeValue, limit);
    }

    public TValue getExtremeValue() {
        return extremeValue.get();
    }

    public TValue put(final TKey key, final TValue value) {
        lock.readLock().lock();

        try {
            if (size.get() < limit || comparator.compare(value, extremeValue.get()) > 0) {
                TValue valueOld = map.put(key, value);

                if (valueOld == null && size.getAndIncrement() >= limit) {
                    map.remove(map.headKey());
                    size.decrementAndGet();
                    extremeValue.set(map.headValue());
                }

                return valueOld;
            }
        } finally {
            lock.readLock().unlock();
        }

        return null;
    }

    public ClearResult clear() {
        lock.writeLock().lock();

        try {
            return new ClearResult(map);
        } finally {
            map = mapFactory.create(comparator);
            keys = Collections.unmodifiableSet(map.keySet());
            extremeValue.set(initialExtremeValue);
            size.set(0);
            lock.writeLock().unlock();
        }
    }

    @FunctionalInterface
    private interface Factory<TKey, TValue> {
        SortedByValueMap<TKey, TValue> create(Comparator<TValue> comparator);
    }

    @RequiredArgsConstructor
    public final class ClearResult {
        private final SortedByValueMap<TKey, TValue> map;

        public List<TKey> retrieve() {
            return map.descendingKeySet()
                    .stream()
                    .limit(limit)
                    .collect(Collectors.toList());
        }
    }
}
