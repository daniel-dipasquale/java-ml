package com.dipasquale.ai.common.sequence;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
@EqualsAndHashCode
public final class StrategySequentialId implements SequentialId, Serializable {
    @Serial
    private static final long serialVersionUID = -1101575222645635713L;
    private final String name;
    private final SequentialId sequentialId;

    private int compareTo(final StrategySequentialId other) {
        int comparison = name.compareTo(other.name);

        if (comparison != 0) {
            return comparison;
        }

        return sequentialId.compareTo(other.sequentialId);
    }

    @Override
    public int compareTo(final SequentialId other) {
        if (other instanceof StrategySequentialId) {
            return compareTo((StrategySequentialId) other);
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
