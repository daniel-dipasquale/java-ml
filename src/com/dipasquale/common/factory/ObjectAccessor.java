package com.dipasquale.common.factory;

@FunctionalInterface
public interface ObjectAccessor<T> {
    T get(final int value);
}
