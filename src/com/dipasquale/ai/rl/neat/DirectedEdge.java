package com.dipasquale.ai.rl.neat;

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

    DirectedEdge(final NodeGene inNode, final NodeGene outNode) {
        this.sourceNodeId = inNode.getId();
        this.targetNodeId = outNode.getId();
    }

    DirectedEdge createReversed() {
        return new DirectedEdge(targetNodeId, sourceNodeId);
    }
}
