package com.dipasquale.data.structure.probabilistic.bloom.filter;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class StrategyBloomFilterPartitionFactoryProxy implements BloomFilterPartitionFactory.Proxy, Serializable {
    @Serial
    private static final long serialVersionUID = -7193742902379231860L;
    private final BloomFilterPartitionFactory bloomFilterPartitionFactory;
    private final int estimatedSize;
    private final int hashingFunctions;
    private final double falsePositiveRatio;
    private final long size;

    @Override
    public <T> BloomFilter<T> create(final int index) {
        return bloomFilterPartitionFactory.create(index, estimatedSize, hashingFunctions, falsePositiveRatio, size);
    }
}
