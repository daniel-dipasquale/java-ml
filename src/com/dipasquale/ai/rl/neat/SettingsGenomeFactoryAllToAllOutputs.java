package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.ContextDefault;
import com.dipasquale.ai.rl.neat.genotype.ConnectionGene;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefaultFactory;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SettingsGenomeFactoryAllToAllOutputs implements GenomeDefaultFactory {
    private final ContextDefault context;
    private final SettingsGenomeFactoryNoConnections genomeFactoryNoConnections;
    private final SettingsFloatNumber weight;
    private final boolean shouldConnectBiasNodes;

    @Override
    public GenomeDefault create() {
        GenomeDefault genome = genomeFactoryNoConnections.create();

        for (NodeGene inputNode : genome.getNodes(NodeGeneType.INPUT)) {
            for (NodeGene outputNode : genome.getNodes(NodeGeneType.OUTPUT)) {
                InnovationId innovationId = context.connections().getOrCreateInnovationId(inputNode, outputNode);
                ConnectionGene connection = new ConnectionGene(innovationId, weight.get());

                genome.addConnection(connection);
            }
        }

        if (shouldConnectBiasNodes) {
            for (NodeGene biasNode : genome.getNodes(NodeGeneType.BIAS)) {
                for (NodeGene outputNode : genome.getNodes(NodeGeneType.OUTPUT)) {
                    InnovationId innovationId = context.connections().getOrCreateInnovationId(biasNode, outputNode);
                    ConnectionGene connection = new ConnectionGene(innovationId, 1f);

                    genome.addConnection(connection);
                }
            }
        }

        return genome;
    }
}
