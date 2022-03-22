package com.dipasquale.common.concurrent;

@FunctionalInterface
public interface RecyclableReferenceFactory<T> {
    T create(long dateTime);
}
