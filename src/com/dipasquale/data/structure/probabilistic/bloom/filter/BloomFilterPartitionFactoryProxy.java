package com.dipasquale.data.structure.probabilistic.bloom.filter;

@FunctionalInterface
public interface BloomFilterPartitionFactoryProxy {
    <T> BloomFilter<T> create(int index);
}
