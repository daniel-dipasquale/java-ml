package com.dipasquale.common;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public final class DefaultLongCounter implements LongCounter, Serializable {
    @Serial
    private static final long serialVersionUID = 8880985247921190483L;
    private long counter = -1L;

    @Override
    public long increment(final long delta) {
        return counter += delta;
    }

    @Override
    public long current() {
        return counter;
    }

    @Override
    public long current(final long value) {
        return counter = value;
    }

    @Override
    public int compareTo(final Long other) {
        return Long.compare(counter, other);
    }

    @Override
    public String toString() {
        return Long.toString(counter);
    }
}
