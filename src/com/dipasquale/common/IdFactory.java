package com.dipasquale.common;

@FunctionalInterface
public interface IdFactory<T> {
    T createId();
}
