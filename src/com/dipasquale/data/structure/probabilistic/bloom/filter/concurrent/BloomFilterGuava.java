package com.dipasquale.data.structure.probabilistic.bloom.filter.concurrent;

import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilter;
import com.google.common.hash.Funnel;

final class BloomFilterGuava<T> implements BloomFilter<T> {
    private com.google.common.hash.BloomFilter<T> bloomFilter;

    public BloomFilterGuava(final int estimatedSize, final double falsePositiveRatio) {
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
