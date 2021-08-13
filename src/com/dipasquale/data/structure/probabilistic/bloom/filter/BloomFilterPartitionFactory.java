/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.probabilistic.bloom.filter;

@FunctionalInterface
public interface BloomFilterPartitionFactory {
    <T> BloomFilter<T> create(int index, int estimatedSize, int hashingFunctions, double falsePositiveRatio, long size);

    @FunctionalInterface
    interface Proxy {
        <T> BloomFilter<T> create(int index);
    }
}
