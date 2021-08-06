package com.dipasquale.data.structure.probabilistic.count.min.sketch;

import com.dipasquale.common.factory.ObjectFactory;

public interface CountMinSketchFactory {
    int getHashingFunctionCount();

    <T> CountMinSketch<T> create(int estimatedSize, final int hashingFunctionCount, double falsePositiveRatio, long size, int bits);

    default <T> ObjectFactory<CountMinSketch<T>> createProxy(final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio, final long size, final int bits) {
        return () -> create(estimatedSize, hashingFunctionCount, falsePositiveRatio, size, bits);
    }

    default <T> CountMinSketch<T> createEstimated(final int estimatedSize, final int hashingFunctionCount, final double falsePositiveRatio, final int bits) {
        return CountMinSketchFactoryAdapter.getInstance().createEstimated(this, estimatedSize, hashingFunctionCount, falsePositiveRatio, bits);
    }

    default <T> CountMinSketch<T> createEstimated(final int estimatedSize, final int hashingFunctionCount, final int bits) {
        return CountMinSketchFactoryAdapter.getInstance().createEstimated(this, estimatedSize, hashingFunctionCount, bits);
    }

    default <T> CountMinSketch<T> createEstimated(final int estimatedSize, final double falsePositiveRatio, final int bits) {
        return CountMinSketchFactoryAdapter.getInstance().createEstimated(this, estimatedSize, falsePositiveRatio, bits);
    }

    default <T> CountMinSketch<T> createEstimated(final int estimatedSize, final int bits) {
        return CountMinSketchFactoryAdapter.getInstance().createEstimated(this, estimatedSize, bits);
    }
}
