package com.dipasquale.common;

public interface IntegerValue extends Comparable<Integer> {
    int current();

    int current(int value);

    int increment(int delta);

    default int increment() {
        return increment(1);
    }

    default int decrement() {
        return increment(-1);
    }

    boolean equals(Object other);

    String toString();
}
