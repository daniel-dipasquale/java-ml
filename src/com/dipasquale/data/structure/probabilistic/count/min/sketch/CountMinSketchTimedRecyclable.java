package com.dipasquale.data.structure.probabilistic.count.min.sketch;

import com.dipasquale.common.ObjectFactory;
import com.dipasquale.common.time.ExpirySupport;
import com.dipasquale.concurrent.AtomicRecyclableReference;
import com.dipasquale.concurrent.RecyclableReference;

final class CountMinSketchTimedRecyclable<T> implements CountMinSketch<T> {
    private final AtomicRecyclableReference<CountMinSketch<T>> recyclableCountMinSketch;

    CountMinSketchTimedRecyclable(final ObjectFactory<CountMinSketch<T>> countMinSketchFactory, final ExpirySupport expirySupport, final RecycledCollector<T> recycledCollector) {
        this.recyclableCountMinSketch = new AtomicRecyclableReference<>(countMinSketchFactory, expirySupport, ensureProxy(recycledCollector));
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
