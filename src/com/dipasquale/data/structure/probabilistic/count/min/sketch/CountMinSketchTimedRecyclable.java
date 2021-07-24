package com.dipasquale.data.structure.probabilistic.count.min.sketch;

import com.dipasquale.common.ObjectFactory;
import com.dipasquale.common.concurrent.AtomicRecyclableReference;
import com.dipasquale.common.concurrent.RecyclableReference;
import com.dipasquale.common.time.ExpirationFactory;

final class CountMinSketchTimedRecyclable<T> implements CountMinSketch<T> {
    private final AtomicRecyclableReference<CountMinSketch<T>> recyclableCountMinSketch;

    CountMinSketchTimedRecyclable(final ObjectFactory<CountMinSketch<T>> countMinSketchFactory, final ExpirationFactory expirationFactory, final RecycledCollector<T> recycledCollector) {
        this.recyclableCountMinSketch = new AtomicRecyclableReference<>(countMinSketchFactory, expirationFactory, ensureProxy(recycledCollector));
    }

    private static <T> RecyclableReference.Collector<CountMinSketch<T>> ensureProxy(final RecycledCollector<T> recycledCollector) {
        if (recycledCollector == null) {
            return null;
        }

        return rr -> recycledCollector.collect(rr.getReference(), rr.getRecycledDateTime());
    }

    @Override
    public long get(final T item) {
        return recyclableCountMinSketch.reference().get(item);
    }

    @Override
    public long put(final T item, final long count) {
        return recyclableCountMinSketch.reference().put(item, count);
    }
}
