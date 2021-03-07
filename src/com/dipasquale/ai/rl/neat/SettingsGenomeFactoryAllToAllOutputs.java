package com.dipasquale.ai.rl.neat;

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
        Iterable<NodeGene> inputNodes = () -> genome.getNodes().iterator(NodeGeneType.Input);
        Iterable<NodeGene> outputNodes = () -> genome.getNodes().iterator(NodeGeneType.Output);

        for (NodeGene inputNode : inputNodes) {
            for (NodeGene outputNode : outputNodes) {
                InnovationId innovationId = context.connections().getOrCreateInnovationId(inputNode, outputNode);
                ConnectionGene connection = new ConnectionGene(innovationId, weight.get());

                genome.addConnection(connection);
            }
        }

        if (shouldConnectBiasNodes) {
            Iterable<NodeGene> biasNodes = () -> genome.getNodes().iterator(NodeGeneType.Bias);

            for (NodeGene biasNode : biasNodes) {
                for (NodeGene outputNode : outputNodes) {
                    InnovationId innovationId = context.connections().getOrCreateInnovationId(biasNode, outputNode);
                    ConnectionGene connection = new ConnectionGene(innovationId, 1f);

                    genome.addConnection(connection);
                }
            }
        }

        return genome;
    }
}
