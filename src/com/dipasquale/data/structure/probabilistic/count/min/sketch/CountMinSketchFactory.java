/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.probabilistic.count.min.sketch;

import com.dipasquale.common.factory.ObjectFactory;

import java.io.Serializable;

public interface CountMinSketchFactory {
    <T> CountMinSketch<T> create(int estimatedSize, final int hashingFunctions, double falsePositiveRatio, long size, int bitsForCounter);

    default <T> ObjectFactory<CountMinSketch<T>> createProxy(final int estimatedSize, final int hashingFunctions, final double falsePositiveRatio, final long size, final int bitsForCounter) {
        return (ObjectFactory<CountMinSketch<T>> & Serializable) () -> create(estimatedSize, hashingFunctions, falsePositiveRatio, size, bitsForCounter);
    }

    default <T> CountMinSketch<T> createEstimated(final int estimatedSize, final int hashingFunctions, final double falsePositiveRatio, final int bitsForCounter) {
        return CountMinSketchFactoryAdapter.createEstimated(this, estimatedSize, hashingFunctions, falsePositiveRatio, bitsForCounter);
    }

    default <T> CountMinSketch<T> createEstimated(final int estimatedSize, final int hashingFunctions, final int bitsForCounter) {
        return CountMinSketchFactoryAdapter.createEstimated(this, estimatedSize, hashingFunctions, bitsForCounter);
    }

    default <T> CountMinSketch<T> createEstimated(final int estimatedSize, final double falsePositiveRatio, final int bitsForCounter) {
        return CountMinSketchFactoryAdapter.createEstimated(this, estimatedSize, falsePositiveRatio, bitsForCounter);
    }

    default <T> CountMinSketch<T> createEstimated(final int estimatedSize, final int bitsForCounter) {
        return CountMinSketchFactoryAdapter.createEstimated(this, estimatedSize, bitsForCounter);
    }
}
