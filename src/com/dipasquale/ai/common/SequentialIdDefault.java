package com.dipasquale.ai.common;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode
public final class SequentialIdDefault implements SequentialId {
    @Serial
    private static final long serialVersionUID = -78487413947636387L;
    private final long value;

    private int compareTo(final SequentialIdDefault other) {
        return Long.compare(value, other.value);
    }

    @Override
    public int compareTo(final SequentialId other) {
        if (other instanceof SequentialIdDefault) {
            return compareTo((SequentialIdDefault) other);
        }

        String otherTypeName = other == null
                ? null
                : other.getClass().getTypeName();

        String message = String.format("unable to compare incompatible sequential ids, x: %s, y: %s", getClass().getTypeName(), otherTypeName);

        throw new IllegalArgumentException(message);
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }
}
