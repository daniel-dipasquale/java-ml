/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.probabilistic.bloom.filter;

public interface BloomFilter<T> {
    boolean mightContain(T item);

    boolean add(T item);
}
