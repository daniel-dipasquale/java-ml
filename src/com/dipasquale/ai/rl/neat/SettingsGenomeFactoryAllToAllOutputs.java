package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.ContextDefault;
import com.dipasquale.ai.rl.neat.genotype.ConnectionGene;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefaultFactory;
import com.dipasquale.ai.rl.neat.genotype.InnovationId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.concurrent.AtomicLazyReference;

final class SettingsGenomeFactoryAllToAllOutputs implements GenomeDefaultFactory {
    private final ContextDefault context;
    private final AtomicLazyReference<GenomeDefault> genesisGenome;
    private final SettingsFloatNumber weight;
    private final boolean shouldConnectBiasNodes;

    SettingsGenomeFactoryAllToAllOutputs(final ContextDefault context, final SettingsGenomeFactoryNoConnections genomeFactoryNoConnections, final SettingsFloatNumber weight, final boolean shouldConnectBiasNodes) {
        this.context = context;
        this.genesisGenome = new AtomicLazyReference<>(genomeFactoryNoConnections::create);
        this.weight = weight;
        this.shouldConnectBiasNodes = shouldConnectBiasNodes;
    }

    @Override
    public GenomeDefault create() {
        GenomeDefault genome = genesisGenome.reference().createClone();

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
