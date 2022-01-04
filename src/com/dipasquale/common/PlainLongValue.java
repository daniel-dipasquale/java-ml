package com.dipasquale.common;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public final class PlainLongValue implements LongValue, Serializable {
    @Serial
    private static final long serialVersionUID = 8880985247921190483L;
    private long raw = -1L;

    @Override
    public long current() {
        return raw;
    }

    @Override
    public long current(final long value) {
        return raw = value;
    }

    @Override
    public long increment(final long delta) {
        return raw += delta;
    }

    @Override
    public int compareTo(final Long other) {
        return Long.compare(raw, other);
    }

    @Override
    public String toString() {
        return Long.toString(raw);
    }
}
