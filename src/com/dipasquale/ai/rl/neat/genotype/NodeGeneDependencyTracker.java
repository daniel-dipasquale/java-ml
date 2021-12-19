package com.dipasquale.ai.rl.neat.genotype;

import java.util.Map;

public interface NodeGeneDependencyTracker {
    void increaseBlastRadius();

    int decreaseBlastRadius();

    void addEdge(DirectedEdge directedEdge);

    void removeEdgesFrom(Map<DirectedEdge, InnovationId> innovationIds);
}
