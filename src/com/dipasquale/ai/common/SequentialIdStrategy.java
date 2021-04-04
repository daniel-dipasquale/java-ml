package com.dipasquale.ai.common;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode
public final class SequentialIdStrategy implements SequentialId {
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

        String message = String.format("unable to compare incompatible sequential ids, x: %s, y: %s", getClass().getTypeName(), other == null ? null : other.getClass().getTypeName());

        throw new IllegalStateException(message);
    }

    @Override
    public String toString() {
        return String.format("%s:%s", name, sequentialId);
    }
}
