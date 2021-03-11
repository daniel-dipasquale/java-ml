package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialId;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class InnovationId implements Comparable<InnovationId> {
    @Getter
    @EqualsAndHashCode.Include
    private final DirectedEdge directedEdge;
    private final SequentialId sequentialId;

    public SequentialId getSourceNodeId() {
        return directedEdge.getSourceNodeId();
    }

    public SequentialId getTargetNodeId() {
        return directedEdge.getTargetNodeId();
    }

    @Override
    public int compareTo(final InnovationId other) {
        return sequentialId.compareTo(other.sequentialId);
    }

    @Override
    public String toString() {
        return String.format("%s:%s", directedEdge, sequentialId);
    }
}
