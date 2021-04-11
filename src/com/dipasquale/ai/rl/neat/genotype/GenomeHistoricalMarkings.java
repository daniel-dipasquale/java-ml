package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.SequentialId;

public interface GenomeHistoricalMarkings {
    String createGenomeId();

    SequentialId createNodeId(NodeGeneType type);

    InnovationId getOrCreateInnovationId(NodeGene inputNode, NodeGene outputNode);
}
