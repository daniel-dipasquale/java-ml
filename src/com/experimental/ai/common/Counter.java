package com.experimental.ai.common;

public interface Counter<T extends Comparable<T>> {
    T next();

    T current();
}
