package com.experimental.ai.common;

@FunctionalInterface
public interface SequentialIdFactory<T extends Comparable<T>> {
    T next();
}
