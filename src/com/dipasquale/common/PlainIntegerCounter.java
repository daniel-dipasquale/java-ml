package com.dipasquale.common;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public final class PlainIntegerCounter implements IntegerCounter, Serializable {
    @Serial
    private static final long serialVersionUID = 4677307812574374001L;
    private int counter = -1;

    @Override
    public int increment(final int delta) {
        return counter += delta;
    }

    @Override
    public int current() {
        return counter;
    }

    @Override
    public int current(final int value) {
        return counter = value;
    }

    @Override
    public int compareTo(final Integer other) {
        return Integer.compare(counter, other);
    }

    @Override
    public String toString() {
        return Integer.toString(counter);
    }
}
