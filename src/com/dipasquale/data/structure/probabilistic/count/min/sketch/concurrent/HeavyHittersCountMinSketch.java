/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.probabilistic.count.min.sketch.concurrent;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.time.ExpirationFactory;
import com.dipasquale.data.structure.map.concurrent.ConcurrentBoundedHeap;
import com.dipasquale.data.structure.probabilistic.count.min.sketch.CountMinSketch;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

final class HeavyHittersCountMinSketch<T> implements CountMinSketch<T>, Serializable {
    @Serial
    private static final long serialVersionUID = -7180234777763394393L;
    private final CountMinSketch<T> countMinSketch;
    private final ConcurrentBoundedHeap<T, Long> heavyHittersHeap;
    private final ReadWriteLock lock;

    HeavyHittersCountMinSketch(final ObjectFactory<CountMinSketch<T>> countMinSketchFactory, final ExpirationFactory expirationFactory, final HeavyHittersCountMinSketchCollector<T> heavyHittersCollector, final int topLimit) {
        ConcurrentBoundedHeap<T, Long> heavyHittersHeap = ConcurrentBoundedHeap.createDescendingOrder(0L, topLimit);
        ReadWriteLock lock = new ReentrantReadWriteLock();
        DefaultRecycledCountMinSketchCollector<T> recycledCollector = new DefaultRecycledCountMinSketchCollector<>(heavyHittersCollector, heavyHittersHeap, lock);

        this.countMinSketch = new RecyclableCountMinSketch<>(countMinSketchFactory, expirationFactory, recycledCollector);
        this.heavyHittersHeap = heavyHittersHeap;
        this.lock = lock;
    }

    @Override
    public long get(final T item) {
        return countMinSketch.get(item);
    }

    @Override
    public long put(final T item, final long count) {
        lock.readLock().lock();

        try {
            long countTotal = countMinSketch.put(item, count);

            heavyHittersHeap.put(item, countTotal);

            return countTotal;
        } finally {
            lock.readLock().unlock();
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class DefaultRecycledCountMinSketchCollector<T> implements RecycledCountMinSketchCollector<T>, Serializable {
        @Serial
        private static final long serialVersionUID = 637258958115809843L;
        private final HeavyHittersCountMinSketchCollector<T> heavyHittersCollector;
        private final ConcurrentBoundedHeap<T, Long> heavyHittersHeap;
        private final ReadWriteLock lock;

        @Override
        public void collect(final CountMinSketch<T> countMinSketch, final long recycledDateTime) {
            lock.writeLock().lock();

            try {
                heavyHittersCollector.collect(countMinSketch, recycledDateTime, heavyHittersHeap.clear().getKeys());
            } finally {
                lock.writeLock().unlock();
            }
        }
    }
}
