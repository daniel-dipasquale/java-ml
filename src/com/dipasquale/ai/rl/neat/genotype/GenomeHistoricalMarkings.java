/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.ai.rl.neat.genotype;

import com.dipasquale.ai.common.sequence.SequentialId;

public interface GenomeHistoricalMarkings {
    String createGenomeId();

    SequentialId createNodeId(NodeGeneType type);

    InnovationId getOrCreateInnovationId(NodeGene inputNode, NodeGene outputNode);
}
