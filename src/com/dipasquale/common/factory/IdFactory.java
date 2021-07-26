package com.dipasquale.common.factory;

@FunctionalInterface
public interface IdFactory<T> {
    T createId();
}
