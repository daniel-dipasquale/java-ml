package com.dipasquale.data.structure.iterable;

import com.dipasquale.data.structure.iterator.FlatIterator;

import java.util.Arrays;

public interface Iterables {
    @SafeVarargs
    static <T> Iterable<T> concatenate(final Iterable<T>... iterables) {
        return () -> FlatIterator.fromIterables(Arrays.stream(iterables)::iterator);
    }
}
