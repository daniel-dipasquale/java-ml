package com.dipasquale.common;

public interface LongCounter extends Comparable<Long> {
    long increment(long delta);

    default long increment() {
        return increment(1L);
    }

    default long decrement() {
        return increment(-1L);
    }

    long current();

    long current(long value);

    boolean equals(Object other);

    String toString();
}
