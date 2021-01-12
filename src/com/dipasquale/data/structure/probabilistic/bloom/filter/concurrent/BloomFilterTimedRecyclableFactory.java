package com.dipasquale.data.structure.probabilistic.bloom.filter.concurrent;

import com.dipasquale.common.ExpirySupport;
import com.dipasquale.common.ObjectFactory;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilter;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilterFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class BloomFilterTimedRecyclableFactory implements BloomFilterFactory {
    private final BloomFilterDefaultFactory bloomFilterDefaultFactory;
    private final ExpirySupport expirySupport;

    @Override
    public int getMaximumHashFunctions() {
        return bloomFilterDefaultFactory.getMaximumHashFunctions();
    }

    @Override
    public <T> BloomFilter<T> create(final int estimatedSize, final int hashFunctions, final double falsePositiveRatio, final long size) {
        ObjectFactory<BloomFilter<T>> bloomFilterFactory = bloomFilterDefaultFactory.createProxy(estimatedSize, hashFunctions, falsePositiveRatio, size);

        return new BloomFilterTimedRecyclable<>(bloomFilterFactory, expirySupport);
    }
}
