package com.dipasquale.ai.common;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.Serial;

@RequiredArgsConstructor
@EqualsAndHashCode
public final class SequentialIdStrategy implements SequentialId {
    @Serial
    private static final long serialVersionUID = 7031781204958031412L;
    private final String name;
    private final SequentialId sequentialId;

    private int compareTo(final SequentialIdStrategy other) {
        int comparison = name.compareTo(other.name);

        if (comparison != 0) {
            return comparison;
        }

        return sequentialId.compareTo(other.sequentialId);
    }

    @Override
    public int compareTo(final SequentialId other) {
        if (other instanceof SequentialIdStrategy) {
            return compareTo((SequentialIdStrategy) other);
        }

        String otherTypeName = other == null
                ? null
                : other.getClass().getTypeName();

        String message = String.format("unable to compare incompatible sequential ids, x: %s, y: %s", getClass().getTypeName(), otherTypeName);

        throw new IllegalArgumentException(message);
    }

    @Override
    public String toString() {
        return String.format("%s:%s", name, sequentialId);
    }
}
