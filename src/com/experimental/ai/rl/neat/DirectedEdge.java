package com.experimental.ai.rl.neat;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public final class DirectedEdge<T> {
    private final T sourceNodeId;
    private final T targetNodeId;

    DirectedEdge(final NodeGene<T> inNode, final NodeGene<T> outNode) {
        this.sourceNodeId = inNode.getId();
        this.targetNodeId = outNode.getId();
    }

    DirectedEdge<T> createReversed() {
        return new DirectedEdge<>(targetNodeId, sourceNodeId);
    }
}
