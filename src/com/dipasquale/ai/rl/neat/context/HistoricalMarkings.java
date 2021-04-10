package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;

public interface HistoricalMarkings {
    String createGenomeId();

    SequentialId createNodeId(NodeGeneType type);

    InnovationId getOrCreateInnovationId(NodeGene inputNode, NodeGene outputNode);
}
