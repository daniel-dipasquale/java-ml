package com.dipasquale.ai.rl.neat.internal;

import com.dipasquale.ai.common.sequence.StrategyLongSequentialId;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
@EqualsAndHashCode
public final class Id implements Comparable<Id>, Serializable {
    @Serial
    private static final long serialVersionUID = 6434796669671390116L;
    private final StrategyLongSequentialId sequentialId;

    @Override
    public int compareTo(final Id other) {
        return sequentialId.compareTo(other.sequentialId);
    }

    @Override
    public String toString() {
        return sequentialId.toString();
    }
}
