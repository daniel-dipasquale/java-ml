/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.data.structure.probabilistic.count.min.sketch;

public interface CountMinSketch<T> {
    long get(T item);

    long put(T item, long count);

    default long put(final T item) {
        return put(item, 1L);
    }

    default boolean mightContain(final T item) {
        return get(item) > 0L;
    }
}
