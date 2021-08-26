package com.dipasquale.common.factory;

@FunctionalInterface
public interface ObjectIndexer<T> {
    T get(int index);
}
