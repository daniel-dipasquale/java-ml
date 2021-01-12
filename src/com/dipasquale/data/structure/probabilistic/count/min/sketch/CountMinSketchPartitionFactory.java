package com.dipasquale.data.structure.probabilistic.count.min.sketch;

public interface CountMinSketchPartitionFactory {
    int getMaximumHashFunctions();

    <T> CountMinSketch<T> create(int index, int estimatedSize, final int hashFunctions, double falsePositiveRatio, long size, int bits);

    @FunctionalInterface
    interface Proxy {
        <T> CountMinSketch<T> create(int index);

        static Proxy create(final CountMinSketchPartitionFactory countMinSketchPartitionFactory, final int estimatedSize, final int hashFunctions, final double falsePositiveRatio, final long size, final int bits) {
            return new Proxy() {
                @Override
                public <T> CountMinSketch<T> create(final int index) {
                    return countMinSketchPartitionFactory.create(index, estimatedSize, hashFunctions, falsePositiveRatio, size, bits);
                }
            };
        }
    }
}
