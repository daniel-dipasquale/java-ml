package com.dipasquale.data.structure.probabilistic.count.min.sketch;

import com.dipasquale.common.factory.ObjectFactory;

public interface CountMinSketchFactory {
    int getMaximumHashFunctions();

    <T> CountMinSketch<T> create(int estimatedSize, final int hashFunctions, double falsePositiveRatio, long size, int bits);

    default <T> ObjectFactory<CountMinSketch<T>> createProxy(final int estimatedSize, final int hashFunctions, final double falsePositiveRatio, final long size, final int bits) {
        return () -> create(estimatedSize, hashFunctions, falsePositiveRatio, size, bits);
    }

    default <T> CountMinSketch<T> createEstimated(final int estimatedSize, final int hashFunctions, final double falsePositiveRatio, final int bits) {
        return CountMinSketchFactoryAdapter.getInstance().createEstimated(this, estimatedSize, hashFunctions, falsePositiveRatio, bits);
    }

    default <T> CountMinSketch<T> createEstimated(final int estimatedSize, final int hashFunctions, final int bits) {
        return CountMinSketchFactoryAdapter.getInstance().createEstimated(this, estimatedSize, hashFunctions, bits);
    }

    default <T> CountMinSketch<T> createEstimated(final int estimatedSize, final double falsePositiveRatio, final int bits) {
        return CountMinSketchFactoryAdapter.getInstance().createEstimated(this, estimatedSize, falsePositiveRatio, bits);
    }

    default <T> CountMinSketch<T> createEstimated(final int estimatedSize, final int bits) {
        return CountMinSketchFactoryAdapter.getInstance().createEstimated(this, estimatedSize, bits);
    }
}
