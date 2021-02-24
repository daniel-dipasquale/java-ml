package com.experimental.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class DirectedEdgeNodePair<T> {
    private final NodeGene<T> inNode;
    private final NodeGene<T> outNode;

    public DirectedEdge<T> createDirectedEdge() {
        return new DirectedEdge<>(inNode.getId(), outNode.getId());
    }
}
