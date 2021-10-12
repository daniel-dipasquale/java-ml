package com.dipasquale.data.structure.probabilistic.bloom.filter;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class StrategyBloomFilterPartitionFactory implements BloomFilterPartitionFactory, Serializable {
    @Serial
    private static final long serialVersionUID = -2183884097024152126L;
    private final BloomFilterFactory bloomFilterFactory;

    @Override
    public <T> BloomFilter<T> create(final int index, final int estimatedSize, final int hashingFunctions, final double falsePositiveRatio, final long size) {
        return bloomFilterFactory.create(estimatedSize, hashingFunctions, falsePositiveRatio, size);
    }
}
