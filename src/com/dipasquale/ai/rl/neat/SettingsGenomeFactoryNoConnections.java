package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.ContextDefault;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefault;
import com.dipasquale.ai.rl.neat.genotype.GenomeDefaultFactory;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.concurrent.AtomicLazyReference;

import java.util.ArrayList;
import java.util.List;

final class SettingsGenomeFactoryNoConnections implements GenomeDefaultFactory {
    private final ContextDefault context;
    private final AtomicLazyReference<List<NodeGene>> inputNodes;
    private final AtomicLazyReference<List<NodeGene>> outputNodes;
    private final AtomicLazyReference<List<NodeGene>> biasNodes;

    SettingsGenomeFactoryNoConnections(final ContextDefault context, final int inputs, final int outputs, final int biases) {
        this.context = context;
        this.inputNodes = new AtomicLazyReference<>(() -> createNodes(context, inputs, NodeGeneType.INPUT));
        this.outputNodes = new AtomicLazyReference<>(() -> createNodes(context, outputs, NodeGeneType.OUTPUT));
        this.biasNodes = new AtomicLazyReference<>(() -> createNodes(context, biases, NodeGeneType.BIAS));
    }

    private static List<NodeGene> createNodes(final ContextDefault context, final int count, final NodeGeneType type) {
        List<NodeGene> nodes = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            NodeGene node = context.nodes().create(type);

            nodes.add(node);
        }

        return nodes;
    }

    @Override
    public GenomeDefault create() {
        GenomeDefault genome = new GenomeDefault(context);

        inputNodes.reference().forEach(genome::addNode);
        outputNodes.reference().forEach(genome::addNode);
        biasNodes.reference().forEach(genome::addNode);

        return genome;
    }
}
