package com.dipasquale.common.factory;

@FunctionalInterface
public interface ObjectFactory<T> {
    T create();
}
