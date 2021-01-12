package com.dipasquale.data.structure.probabilistic.bloom.filter;

public interface BloomFilter<T> {
    boolean mightContain(T item);

    boolean add(T item);
}
