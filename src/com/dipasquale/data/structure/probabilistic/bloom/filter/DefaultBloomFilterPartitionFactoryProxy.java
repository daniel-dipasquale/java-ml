package com.dipasquale.data.structure.probabilistic.bloom.filter;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class DefaultBloomFilterPartitionFactoryProxy implements BloomFilterPartitionFactoryProxy, Serializable {
    @Serial
    private static final long serialVersionUID = -692288823266308310L;
    private final BloomFilterPartitionFactory bloomFilterPartitionFactory;
    private final int estimatedSize;
    private final int hashingFunctionCount;
    private final double falsePositiveRatio;
    private final long size;

    @Override
    public <T> BloomFilter<T> create(final int index) {
        return bloomFilterPartitionFactory.create(index, estimatedSize, hashingFunctionCount, falsePositiveRatio, size);
    }
}
