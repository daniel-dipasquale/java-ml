package com.dipasquale.common;

public interface IntegerCounter extends Comparable<Integer> {
    int increment(int delta);

    default int increment() {
        return increment(1);
    }

    default int decrement() {
        return increment(-1);
    }

    int current();

    int current(int value);

    boolean equals(Object other);

    String toString();
}
