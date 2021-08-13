/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.probabilistic.count.min.sketch;

@FunctionalInterface
public interface CountMinSketchPartitionFactory {
    <T> CountMinSketch<T> create(int index, int estimatedSize, final int hashingFunctions, double falsePositiveRatio, long size, int bitsForCounter);

    @FunctionalInterface
    interface Proxy {
        <T> CountMinSketch<T> create(int index);
    }
}
