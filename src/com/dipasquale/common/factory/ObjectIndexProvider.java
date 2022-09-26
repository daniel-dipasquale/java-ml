package com.dipasquale.common.factory;

@FunctionalInterface
public interface ObjectIndexProvider<T> {
    T provide(int index);
}
