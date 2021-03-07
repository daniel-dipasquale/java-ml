package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialId;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
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
}
