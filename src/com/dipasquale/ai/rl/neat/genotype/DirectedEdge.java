package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.rl.neat.common.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public final class DirectedEdge implements Serializable {
    @Serial
    private static final long serialVersionUID = 5476428602513687108L;
    private final Id sourceNodeId;
    private final Id targetNodeId;

    public DirectedEdge(final NodeGene sourceNode, final NodeGene targetNode) {
        this.sourceNodeId = sourceNode.getId();
        this.targetNodeId = targetNode.getId();
    }

    @Override
    public String toString() {
        return String.format("%s:%s", sourceNodeId, targetNodeId);
    }
}
