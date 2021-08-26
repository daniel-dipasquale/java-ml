package com.dipasquale.ai.rl.neat.genotype;

import java.util.Map;

public interface NodeGeneIdDependencyTracker {
    void increaseBlastRadius();

    int decreaseBlastRadius();

    void add(DirectedEdge directedEdge);

    void removeFrom(Map<DirectedEdge, InnovationId> innovationIds);
}
