package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.common.ActivationFunction;
import com.dipasquale.ai.common.ActivationFunctionFactory;
import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.common.FloatFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class ContextDefaultNodeGeneSupport implements Context.NodeGeneSupport {
    private final Map<NodeGeneType, SequentialIdFactory> sequentialIdFactories;
    private final Map<NodeGeneType, FloatFactory> biasFactories;
    private final Map<NodeGeneType, ActivationFunctionFactory> activationFunctionFactories;
    private final List<NodeGene> inputNodes;
    private final List<NodeGene> outputNodes;
    private final List<NodeGene> biasNodes;

    public ContextDefaultNodeGeneSupport(final Map<NodeGeneType, SequentialIdFactory> sequentialIdFactories, final Map<NodeGeneType, FloatFactory> biasFactories, final Map<NodeGeneType, ActivationFunctionFactory> activationFunctionFactories, final int inputs, final int outputs, final int biases) {
        this.sequentialIdFactories = sequentialIdFactories;
        this.biasFactories = biasFactories;
        this.activationFunctionFactories = activationFunctionFactories;
        this.inputNodes = Collections.unmodifiableList(createNodes(sequentialIdFactories, biasFactories, activationFunctionFactories, inputs, NodeGeneType.INPUT));
        this.outputNodes = Collections.unmodifiableList(createNodes(sequentialIdFactories, biasFactories, activationFunctionFactories, outputs, NodeGeneType.OUTPUT));
        this.biasNodes = Collections.unmodifiableList(createNodes(sequentialIdFactories, biasFactories, activationFunctionFactories, biases, NodeGeneType.BIAS));
    }

    private static NodeGene create(final Map<NodeGeneType, SequentialIdFactory> sequentialIdFactories, final Map<NodeGeneType, FloatFactory> biasFactories, final Map<NodeGeneType, ActivationFunctionFactory> activationFunctionFactories, final NodeGeneType type) {
        SequentialId id = sequentialIdFactories.get(type).next();
        float bias = biasFactories.get(type).create();
        ActivationFunction activationFunction = activationFunctionFactories.get(type).next();

        return new NodeGene(id, type, bias, activationFunction);
    }

    @Override
    public NodeGene create(final NodeGeneType type) {
        return create(sequentialIdFactories, biasFactories, activationFunctionFactories, type);
    }

    private static List<NodeGene> createNodes(final Map<NodeGeneType, SequentialIdFactory> sequentialIdFactories, final Map<NodeGeneType, FloatFactory> biasFactories, final Map<NodeGeneType, ActivationFunctionFactory> activationFunctionFactories, final int count, final NodeGeneType type) {
        List<NodeGene> nodes = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            NodeGene node = create(sequentialIdFactories, biasFactories, activationFunctionFactories, type);

            nodes.add(node);
        }

        return nodes;
    }

    @Override
    public List<NodeGene> inputNodes() {
        return inputNodes;
    }

    @Override
    public List<NodeGene> outputNodes() {
        return outputNodes;
    }

    @Override
    public List<NodeGene> biasNodes() {
        return biasNodes;
    }

    @Override
    public void reset() {
        sequentialIdFactories.values().forEach(SequentialIdFactory::reset);
    }
}
