package com.dipasquale.ai.rl.neat;

import com.dipasquale.concurrent.AtomicLazyReference;

final class SettingsGenomeFactoryAllToAllOutputs<T extends Comparable<T>> implements GenomeDefaultFactory<T> {
    private final ContextDefault<T> context;
    private final AtomicLazyReference<GenomeDefault<T>> genesisGenome;
    private final SettingsFloatNumber weight;
    private final boolean shouldConnectBiasNodes;

    SettingsGenomeFactoryAllToAllOutputs(final ContextDefault<T> context, final SettingsGenomeFactoryNoConnections<T> genomeFactoryNoConnections, final SettingsFloatNumber weight, final boolean shouldConnectBiasNodes) {
        this.context = context;
        this.genesisGenome = new AtomicLazyReference<>(genomeFactoryNoConnections::create);
        this.weight = weight;
        this.shouldConnectBiasNodes = shouldConnectBiasNodes;
    }

    @Override
    public GenomeDefault<T> create() {
        GenomeDefault<T> genome = genesisGenome.reference().createClone();
        Iterable<NodeGene<T>> inputNodes = () -> genome.getNodes().iterator(NodeGeneType.Input);
        Iterable<NodeGene<T>> outputNodes = () -> genome.getNodes().iterator(NodeGeneType.Output);

        for (NodeGene<T> inputNode : inputNodes) {
            for (NodeGene<T> outputNode : outputNodes) {
                InnovationId<T> innovationId = context.connections().getOrCreateInnovationId(inputNode, outputNode);
                ConnectionGene<T> connection = new ConnectionGene<>(innovationId, weight.get());

                genome.addConnection(connection);
            }
        }

        if (shouldConnectBiasNodes) {
            Iterable<NodeGene<T>> biasNodes = () -> genome.getNodes().iterator(NodeGeneType.Bias);

            for (NodeGene<T> biasNode : biasNodes) {
                for (NodeGene<T> outputNode : outputNodes) {
                    InnovationId<T> innovationId = context.connections().getOrCreateInnovationId(biasNode, outputNode);
                    ConnectionGene<T> connection = new ConnectionGene<>(innovationId, 1f);

                    genome.addConnection(connection);
                }
            }
        }

        return genome;
    }
}
