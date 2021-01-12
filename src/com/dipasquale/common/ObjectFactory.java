package com.dipasquale.common;

@FunctionalInterface
public interface ObjectFactory<T> {
    T create();
}
