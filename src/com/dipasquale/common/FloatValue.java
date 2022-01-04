package com.dipasquale.common;

public interface FloatValue extends Comparable<Float> {
    float current();

    float current(float value);

    float increment(float delta);

    boolean equals(Object other);

    String toString();
}
