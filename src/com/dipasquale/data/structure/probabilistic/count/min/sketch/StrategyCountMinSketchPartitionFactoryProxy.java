package com.dipasquale.data.structure.probabilistic.count.min.sketch;

import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
public final class StrategyCountMinSketchPartitionFactoryProxy implements CountMinSketchPartitionFactory.Proxy, Serializable {
    @Serial
    private static final long serialVersionUID = 8181382817016169961L;
    private final CountMinSketchPartitionFactory countMinSketchPartitionFactory;
    private final int estimatedSize;
    private final int hashingFunctions;
    private final double falsePositiveRate;
    private final long size;
    private final int bitsForCounter;

    @Override
    public <T> CountMinSketch<T> create(final int index) {
        return countMinSketchPartitionFactory.create(index, estimatedSize, hashingFunctions, falsePositiveRate, size, bitsForCounter);
    }
}
