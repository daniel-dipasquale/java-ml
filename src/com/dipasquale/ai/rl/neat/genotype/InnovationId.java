package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.rl.neat.internal.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class InnovationId implements Comparable<InnovationId>, Serializable {
    @Serial
    private static final long serialVersionUID = -8131545172613859588L;
    @Getter
    @EqualsAndHashCode.Include
    private final DirectedEdge directedEdge;
    private final Id sequentialId;

    public Id getSourceNodeId() {
        return directedEdge.getSourceNodeId();
    }

    public Id getTargetNodeId() {
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
