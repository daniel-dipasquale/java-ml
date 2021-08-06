package com.dipasquale.data.structure.probabilistic.bloom.filter;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class DefaultBloomFilterPartitionFactory implements BloomFilterPartitionFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -2183884097024152126L;
    private final BloomFilterFactory bloomFilterFactory;

    @Override
    public int getHashingFunctionCount() {
        return bloomFilterFactory.getHashingFunctionCount();
    }

    @Override
    public <T> BloomFilter<T> create(final int index, final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio, final long size) {
        return bloomFilterFactory.create(estimatedSize, hashingFunctionCount, falsePositiveRatio, size);
    }
}
