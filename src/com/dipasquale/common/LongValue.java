package com.dipasquale.common;

public interface LongValue extends Comparable<Long> {
    long current();

    long current(long value);

    long increment(long delta);

    default long increment() {
        return increment(1L);
    }

    default long decrement() {
        return increment(-1L);
    }

    boolean equals(Object other);

    String toString();
}
