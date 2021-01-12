package com.dipasquale.data.structure.probabilistic.bloom.filter.concurrent;

import com.dipasquale.common.ExpirySupport;
import com.dipasquale.common.ObjectFactory;
import com.dipasquale.concurrent.AtomicRecyclableReference;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilter;

final class BloomFilterTimedRecyclable<T> implements BloomFilter<T> {
    private final AtomicRecyclableReference<BloomFilter<T>> recyclableBloomFilter;

    BloomFilterTimedRecyclable(final ObjectFactory<BloomFilter<T>> bloomFilterFactory, final ExpirySupport expirySupport) {
        this.recyclableBloomFilter = new AtomicRecyclableReference<>(bloomFilterFactory, expirySupport);
    }

    @Override
    public boolean mightContain(final T item) {
        return recyclableBloomFilter.reference().mightContain(item);
    }

    @Override
    public boolean add(final T item) {
        return recyclableBloomFilter.reference().add(item);
    }
}
