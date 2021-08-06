package com.dipasquale.data.structure.probabilistic.bloom.filter;

public interface BloomFilterPartitionFactory {
    int getHashingFunctionCount();

    <T> BloomFilter<T> create(int index, int estimatedSize, int hashingFunctionCount, double falsePositiveRatio, long size);
}
