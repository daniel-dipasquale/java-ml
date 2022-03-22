package com.dipasquale.common.concurrent;

@FunctionalInterface
public interface RecyclableReferenceCollector<T> {
    void collect(T reference, RuntimeException exception, long recycledDateTime);
}
