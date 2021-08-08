package com.dipasquale.data.structure.probabilistic.count.min.sketch;

@FunctionalInterface
public interface CountMinSketchPartitionFactory {
    <T> CountMinSketch<T> create(int index, int estimatedSize, final int hashingFunctions, double falsePositiveRatio, long size, int bitsForCounter);
}
