package com.dipasquale.data.structure.probabilistic.bloom.filter;

import com.dipasquale.common.concurrent.AtomicRecyclableReference;
import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.time.ExpirationFactory;

final class BloomFilterTimedRecyclable<T> implements BloomFilter<T> {
    private final AtomicRecyclableReference<BloomFilter<T>> recyclableBloomFilter;

    BloomFilterTimedRecyclable(final ObjectFactory<BloomFilter<T>> bloomFilterFactory, final ExpirationFactory expirationFactory) {
        this.recyclableBloomFilter = new AtomicRecyclableReference<>(bloomFilterFactory, expirationFactory);
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
