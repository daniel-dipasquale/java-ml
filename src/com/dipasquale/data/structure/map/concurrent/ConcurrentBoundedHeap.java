/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.map.concurrent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class ConcurrentBoundedHeap<TKey, TValue extends Comparable<TValue>> implements Serializable {
    @Serial
    private static final long serialVersionUID = 611601432829176622L;
    private final Factory<TKey, TValue> sortedByValueMapFactory;
    private final Comparator<TValue> comparator;
    private ConcurrentSortedByValueMap<TKey, TValue> sortedByValueMap;
    @Getter
    private Set<TKey> keys;
    private final TValue initialFirstValue;
    @Getter
    private volatile TValue firstValue;
    private final int limit;
    private final AtomicInteger size;
    private final ReadWriteLock lock;

    private ConcurrentBoundedHeap(final Factory<TKey, TValue> sortedByValueMapFactory, final Comparator<TValue> comparator, final TValue initialFirstValue, final int limit) {
        ConcurrentSortedByValueMap<TKey, TValue> sortedByValueMap = sortedByValueMapFactory.create(comparator);

        this.sortedByValueMapFactory = sortedByValueMapFactory;
        this.comparator = comparator;
        this.sortedByValueMap = sortedByValueMap;
        this.keys = Collections.unmodifiableSet(sortedByValueMap.keySet());
        this.initialFirstValue = initialFirstValue;
        this.firstValue = initialFirstValue;
        this.limit = limit;
        this.size = new AtomicInteger();
        this.lock = new ReentrantReadWriteLock();
    }

    public static <T> ConcurrentBoundedHeap<T, Long> createDescendingOrder(final long initialFirstValue, final int limit) {
        Factory<T, Long> mapFactory = ConcurrentSortedByValueMap::new;
        Comparator<Long> comparator = Long::compare;

        return new ConcurrentBoundedHeap<>(mapFactory, comparator, initialFirstValue, limit);
    }

    public TValue put(final TKey key, final TValue value) {
        lock.readLock().lock();

        try {
            if (size.get() < limit || comparator.compare(value, firstValue) > 0) {
                TValue valueOld = sortedByValueMap.put(key, value);

                if (valueOld == null && size.getAndIncrement() >= limit) {
                    sortedByValueMap.remove(sortedByValueMap.headKey());
                    size.decrementAndGet();
                    firstValue = sortedByValueMap.headValue();
                }

                return valueOld;
            }
        } finally {
            lock.readLock().unlock();
        }

        return null;
    }

    public ClearResult<TKey, TValue> clear() {
        lock.writeLock().lock();

        try {
            return new ClearResult<>(sortedByValueMap);
        } finally {
            sortedByValueMap = sortedByValueMapFactory.create(comparator);
            keys = Collections.unmodifiableSet(sortedByValueMap.keySet());
            firstValue = initialFirstValue;
            size.set(0);
            lock.writeLock().unlock();
        }
    }

    @FunctionalInterface
    private interface Factory<TKey, TValue> extends Serializable {
        ConcurrentSortedByValueMap<TKey, TValue> create(Comparator<TValue> comparator);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ClearResult<TKey, TValue> implements Serializable {
        @Serial
        private static final long serialVersionUID = 2527785575818134933L;
        private final ConcurrentSortedByValueMap<TKey, TValue> sortedByValueMap;

        public List<TKey> getKeys() {
            return new ArrayList<>(sortedByValueMap.descendingKeySet());
        }
    }
}
