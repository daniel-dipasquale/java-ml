package com.dipasquale.common.factory;

@FunctionalInterface
public interface ObjectIndexAccessor<T> {
    T get(int index);
}
