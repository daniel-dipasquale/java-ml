package com.dipasquale.common.factory;

@FunctionalInterface
public interface ObjectIndexReader<T> {
    T get(int index);
}
