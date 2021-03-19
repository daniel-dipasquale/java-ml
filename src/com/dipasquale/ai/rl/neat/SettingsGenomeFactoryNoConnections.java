package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.ContextDefault;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefaultFactory;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SettingsGenomeFactoryNoConnections implements GenomeDefaultFactory {
    private final ContextDefault context;
    private final int inputs;
    private final int outputs;
    private final int biases;

    @Override
    public GenomeDefault create() {
        GenomeDefault genome = new GenomeDefault(context);

        for (int i = 0; i < inputs; i++) {
            NodeGene node = context.nodes().create(NodeGeneType.Input);

            genome.addNode(node);
        }

        for (int i = 0; i < outputs; i++) {
            NodeGene node = context.nodes().create(NodeGeneType.Output);

            genome.addNode(node);
        }

        for (int i = 0; i < biases; i++) {
            NodeGene node = context.nodes().create(NodeGeneType.Bias);

            genome.addNode(node);
        }

        return genome;
    }
}
