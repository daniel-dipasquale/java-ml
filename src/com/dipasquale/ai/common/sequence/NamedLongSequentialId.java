package com.dipasquale.ai.common.sequence;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
@EqualsAndHashCode
public final class NamedLongSequentialId implements SequentialId<NamedLongSequentialId>, Serializable {
    @Serial
    private static final long serialVersionUID = -1101575222645635713L;
    private final String name;
    private final LongSequentialId sequentialId;

    @Override
    public int compareTo(final NamedLongSequentialId other) {
        int comparison = name.compareTo(other.name);

        if (comparison != 0) {
            return comparison;
        }

        return sequentialId.compareTo(other.sequentialId);
    }

    @Override
    public String toString() {
        return String.format("%s-%s", name, sequentialId);
    }
}
