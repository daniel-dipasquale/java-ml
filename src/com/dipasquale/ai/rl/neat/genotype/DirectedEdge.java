package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.SequentialId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public final class DirectedEdge {
    private final SequentialId sourceNodeId;
    private final SequentialId targetNodeId;

    public DirectedEdge(final NodeGene inNode, final NodeGene outNode) {
        this.sourceNodeId = inNode.getId();
        this.targetNodeId = outNode.getId();
    }

    @Override
    public String toString() {
        return String.format("%s-%s", sourceNodeId, targetNodeId);
    }
}