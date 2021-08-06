package com.dipasquale.data.structure.probabilistic.bloom.filter.concurrent;

import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilter;
import com.google.common.hash.Funnel;

import java.io.Serial;
import java.io.Serializable;

final class SynchronizedGuavaBloomFilter<T> implements BloomFilter<T>, Serializable {
    @Serial
    private static final long serialVersionUID = -7988783923116325694L;
    private com.google.common.hash.BloomFilter<T> bloomFilter;

    SynchronizedGuavaBloomFilter(final int estimatedSize, final double falsePositiveRatio) {
        Funnel<T> funnel = (f, i) -> i.putInt(f.hashCode());

        this.bloomFilter = com.google.common.hash.BloomFilter.create(funnel, estimatedSize, falsePositiveRatio);
    }

    @Override
    public boolean mightContain(final T item) {
        synchronized (bloomFilter) {
            return bloomFilter.mightContain(item);
        }
    }

    @Override
    public boolean add(final T item) {
        synchronized (bloomFilter) {
            return bloomFilter.put(item);
        }
    }
}
