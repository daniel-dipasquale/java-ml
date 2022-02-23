package com.dipasquale.data.structure.probabilistic.count.min.sketch.concurrent;

import com.dipasquale.common.concurrent.AtomicRecyclableReference;
import com.dipasquale.common.concurrent.RecyclableReference;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.time.ExpirationFactory;
import com.dipasquale.data.structure.probabilistic.count.min.sketch.CountMinSketch;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

final class RecyclableCountMinSketch<T> implements CountMinSketch<T>, Serializable {
    @Serial
    private static final long serialVersionUID = 7408778685481838033L;
    private final AtomicRecyclableReference<CountMinSketch<T>> recyclableCountMinSketch;

    private static <T> RecyclableReference.Collector<CountMinSketch<T>> ensureProxy(final RecycledCountMinSketchCollector<T> recycledCollector) {
        if (recycledCollector == null) {
            return null;
        }

        return new RecyclableCountMinSketchCollector<>(recycledCollector);
    }

    RecyclableCountMinSketch(final ObjectFactory<CountMinSketch<T>> countMinSketchFactory, final ExpirationFactory expirationFactory, final RecycledCountMinSketchCollector<T> recycledCollector) {
        this.recyclableCountMinSketch = new AtomicRecyclableReference<>(countMinSketchFactory, expirationFactory, ensureProxy(recycledCollector));
    }

    @Override
    public long get(final T item) {
        return recyclableCountMinSketch.reference().get(item);
    }

    @Override
    public long put(final T item, final long count) {
        return recyclableCountMinSketch.reference().put(item, count);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class RecyclableCountMinSketchCollector<T> implements RecyclableReference.Collector<CountMinSketch<T>>, Serializable {
        @Serial
        private static final long serialVersionUID = -1600453474656786710L;
        private final RecycledCountMinSketchCollector<T> recycledCollector;

        @Override
        public void collect(final RecyclableReference<CountMinSketch<T>> reference) {
            recycledCollector.collect(reference.getReference(), reference.getRecycledDateTime());
        }
    }
}
