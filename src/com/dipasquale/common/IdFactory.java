package com.dipasquale.common;

@FunctionalInterface
public interface IdFactory<T> {
    T createId();

    static IdFactory<Long> createThreadIdFactory() {
        return () -> Thread.currentThread().getId();
    }
}
