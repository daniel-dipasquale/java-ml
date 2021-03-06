package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SettingsGenomeFactoryNoConnections<T extends Comparable<T>> implements GenomeDefaultFactory<T> {
    private final ContextDefault<T> context;
    private final int inputs;
    private final int outputs;
    private final int biases;

    @Override
    public GenomeDefault<T> create() {
        GenomeDefault<T> genome = new GenomeDefault<>(context);

        for (int i = 0; i < inputs; i++) {
            NodeGene<T> node = context.nodes().create(NodeGeneType.Input);

            genome.addNode(node);
        }

        for (int i = 0; i < outputs; i++) {
            NodeGene<T> node = context.nodes().create(NodeGeneType.Output);

            genome.addNode(node);
        }

        for (int i = 0; i < biases; i++) {
            NodeGene<T> node = context.nodes().create(NodeGeneType.Bias);

            genome.addNode(node);
        }

        return genome;
    }
}
