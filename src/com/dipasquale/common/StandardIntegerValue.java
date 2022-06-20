package com.dipasquale.common;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public final class StandardIntegerValue implements IntegerValue, Serializable {
    @Serial
    private static final long serialVersionUID = 4677307812574374001L;
    private int raw = -1;

    @Override
    public int current() {
        return raw;
    }

    @Override
    public int current(final int value) {
        return raw = value;
    }

    @Override
    public int increment(final int delta) {
        return raw += delta;
    }

    @Override
    public int compareTo(final Integer other) {
        return Integer.compare(raw, other);
    }

    @Override
    public String toString() {
        return Integer.toString(raw);
    }
}
