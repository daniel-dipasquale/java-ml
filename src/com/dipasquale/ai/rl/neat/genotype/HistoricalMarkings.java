package com.dipasquale.ai.rl.neat.genotype;

public interface HistoricalMarkings {
    InnovationId getOrCreateInnovationId(DirectedEdge directedEdge);

    boolean containsInnovationId(DirectedEdge directedEdge);

    void registerNode(NodeGene node);

    void deregisterNode(NodeGene node);

    void clear();
}
