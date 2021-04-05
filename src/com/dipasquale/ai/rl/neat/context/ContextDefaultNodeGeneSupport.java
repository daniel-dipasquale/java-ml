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
    private final int inputs;
    private final List<NodeGene> inputNodes;
    private final List<NodeGene> inputNodesReadOnly;
    private final int outputs;
    private final List<NodeGene> outputNodes;
    private final List<NodeGene> outputNodesReadOnly;
    private final int biases;
    private final List<NodeGene> biasNodes;
    private final List<NodeGene> biasNodesReadOnly;

    public ContextDefaultNodeGeneSupport(final Map<NodeGeneType, SequentialIdFactory> sequentialIdFactories, final Map<NodeGeneType, FloatFactory> biasFactories, final Map<NodeGeneType, ActivationFunctionFactory> activationFunctionFactories, final int inputs, final int outputs, final int biases) {
        List<NodeGene> inputNodes = createNodes(sequentialIdFactories, biasFactories, activationFunctionFactories, inputs, NodeGeneType.INPUT);
        List<NodeGene> outputNodes = createNodes(sequentialIdFactories, biasFactories, activationFunctionFactories, outputs, NodeGeneType.OUTPUT);
        List<NodeGene> biasNodes = createNodes(sequentialIdFactories, biasFactories, activationFunctionFactories, biases, NodeGeneType.BIAS);

        this.sequentialIdFactories = sequentialIdFactories;
        this.biasFactories = biasFactories;
        this.activationFunctionFactories = activationFunctionFactories;
        this.inputs = inputs;
        this.inputNodes = inputNodes;
        this.inputNodesReadOnly = Collections.unmodifiableList(inputNodes);
        this.outputs = outputs;
        this.outputNodes = outputNodes;
        this.outputNodesReadOnly = Collections.unmodifiableList(outputNodes);
        this.biases = biases;
        this.biasNodes = biasNodes;
        this.biasNodesReadOnly = Collections.unmodifiableList(biasNodes);
    }

    private static NodeGene create(final Map<NodeGeneType, SequentialIdFactory> sequentialIdFactories, final Map<NodeGeneType, FloatFactory> biasFactories, final Map<NodeGeneType, ActivationFunctionFactory> activationFunctionFactories, final NodeGeneType type) {
        SequentialId id = sequentialIdFactories.get(type).create();
        float bias = biasFactories.get(type).create();
        ActivationFunction activationFunction = activationFunctionFactories.get(type).create();

        return new NodeGene(id, type, bias, activationFunction);
    }

    @Override
    public NodeGene create(final NodeGeneType type) {
        return create(sequentialIdFactories, biasFactories, activationFunctionFactories, type);
    }

    private static void fillNodes(final List<NodeGene> nodes, final Map<NodeGeneType, SequentialIdFactory> sequentialIdFactories, final Map<NodeGeneType, FloatFactory> biasFactories, final Map<NodeGeneType, ActivationFunctionFactory> activationFunctionFactories, final int count, final NodeGeneType type) {
        for (int i = 0; i < count; i++) {
            NodeGene node = create(sequentialIdFactories, biasFactories, activationFunctionFactories, type);

            nodes.add(node);
        }
    }

    private static List<NodeGene> createNodes(final Map<NodeGeneType, SequentialIdFactory> sequentialIdFactories, final Map<NodeGeneType, FloatFactory> biasFactories, final Map<NodeGeneType, ActivationFunctionFactory> activationFunctionFactories, final int count, final NodeGeneType type) {
        List<NodeGene> nodes = new ArrayList<>();

        fillNodes(nodes, sequentialIdFactories, biasFactories, activationFunctionFactories, count, type);

        return nodes;
    }

    @Override
    public List<NodeGene> inputNodes() {
        return inputNodesReadOnly;
    }

    @Override
    public List<NodeGene> outputNodes() {
        return outputNodesReadOnly;
    }

    @Override
    public List<NodeGene> biasNodes() {
        return biasNodesReadOnly;
    }

    @Override
    public void reset() {
        sequentialIdFactories.values().forEach(SequentialIdFactory::reset);
        inputNodes.clear();
        fillNodes(inputNodes, sequentialIdFactories, biasFactories, activationFunctionFactories, inputs, NodeGeneType.INPUT);
        outputNodes.clear();
        fillNodes(outputNodes, sequentialIdFactories, biasFactories, activationFunctionFactories, outputs, NodeGeneType.OUTPUT);
        biasNodes.clear();
        fillNodes(biasNodes, sequentialIdFactories, biasFactories, activationFunctionFactories, biases, NodeGeneType.BIAS);
    }
}
