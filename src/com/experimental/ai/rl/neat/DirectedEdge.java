package com.experimental.ai.rl.neat;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public final class DirectedEdge<T> {
    private final T inNodeId;
    private final T outNodeId;

    DirectedEdge(final NodeGene<T> inNode, final NodeGene<T> outNode) {
        this.inNodeId = inNode.getId();
        this.outNodeId = outNode.getId();
    }

    DirectedEdge<T> createReversed() {
        return new DirectedEdge<>(outNodeId, inNodeId);
    }
}
