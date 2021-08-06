package com.dipasquale.data.structure.probabilistic.bloom.filter.concurrent;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.common.time.ExpirationFactory;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilter;
import com.dipasquale.data.structure.probabilistic.bloom.filter.BloomFilterFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class RecyclableBloomFilterFactory implements BloomFilterFactory, Serializable {
    @Serial
    private static final long serialVersionUID = 6740732055479399531L;
    private final ConcurrentBloomFilterFactory concurrentBloomFilterFactory;
    private final ExpirationFactory expirationFactory;

    @Override
    public int getHashingFunctionCount() {
        return concurrentBloomFilterFactory.getHashingFunctionCount();
    }

    @Override
    public <T> BloomFilter<T> create(final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio, final long size) {
        ObjectFactory<BloomFilter<T>> bloomFilterFactory = concurrentBloomFilterFactory.createProxy(estimatedSize, hashingFunctionCount, falsePositiveRatio, size);

        return new RecyclableBloomFilter<>(bloomFilterFactory, expirationFactory);
    }
}
