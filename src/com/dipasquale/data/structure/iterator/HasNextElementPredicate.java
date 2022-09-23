package com.dipasquale.data.structure.iterator;

@FunctionalInterface
public interface HasNextElementPredicate<T> {
    boolean hasMore(T element);
}
