package com.dipasquale.ai.common.sequence;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
@EqualsAndHashCode
public final class NumberSequentialId implements SequentialId<NumberSequentialId>, Serializable {
    @Serial
    private static final long serialVersionUID = 8593186826114247631L;
    private final long value;

    @Override
    public int compareTo(final NumberSequentialId other) {
        return Long.compare(value, other.value);
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }
}
