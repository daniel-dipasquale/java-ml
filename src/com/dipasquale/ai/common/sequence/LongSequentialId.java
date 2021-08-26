package com.dipasquale.ai.common.sequence;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode
public final class LongSequentialId implements SequentialId, Serializable {
    @Serial
    private static final long serialVersionUID = 8593186826114247631L;
    private final long value;

    private int compareTo(final LongSequentialId other) {
        return Long.compare(value, other.value);
    }

    @Override
    public int compareTo(final SequentialId other) {
        if (other instanceof LongSequentialId) { // TODO: not great to have to do this >.<
            return compareTo((LongSequentialId) other);
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
