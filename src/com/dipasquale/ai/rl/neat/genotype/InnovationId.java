/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.sequence.SequentialId;
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
