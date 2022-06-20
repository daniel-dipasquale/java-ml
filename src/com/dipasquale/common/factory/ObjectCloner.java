package com.dipasquale.common.factory;

@FunctionalInterface
public interface ObjectCloner<T> {
    T clone(T object);
}
