package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.ConnectionGeneMap;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneMap;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.factory.ObjectFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public final class DefaultNeuralNetwork implements NeuralNetwork {
    private final NodeGeneMap nodes;
    private final ConnectionGeneMap connections;
    private final NeuronNavigator neuronNavigator;
    private final AtomicBoolean neuronNavigatorInitialized;
    private volatile boolean neuronNavigatorFinalized;
    private final NeuronFactory neuronFactory;
    private final ObjectFactory<NeuronValueMap> neuronValuesFactory;

    public DefaultNeuralNetwork(final NodeGeneMap nodes, final ConnectionGeneMap connections, final NeuronPathBuilder neuronPathBuilder, final NeuronFactory neuronFactory, final ObjectFactory<NeuronValueMap> neuronValuesFactory) {
        this.nodes = nodes;
        this.connections = connections;
        this.neuronNavigator = new NeuronNavigator(neuronPathBuilder);
        this.neuronNavigatorInitialized = new AtomicBoolean(false);
        this.neuronNavigatorFinalized = false;
        this.neuronFactory = neuronFactory;
        this.neuronValuesFactory = neuronValuesFactory;
    }

    private Neuron createNeuron(final NodeGene node) {
        List<InputNeuron> inputs = connections.getIncomingToNodeFromExpressed(node).values().stream()
                .map(c -> new InputNeuron(c.getInnovationId().getSourceNodeId(), c.getRecurrentCyclesAllowed()))
                .collect(Collectors.toList());

        List<OutputNeuron> outputs = connections.getOutgoingFromNodeFromExpressed(node).values().stream()
                .map(c -> new OutputNeuron(c.getInnovationId().getTargetNodeId(), c.getWeight()))
                .collect(Collectors.toList());

        switch (node.getType()) {
            case BIAS:
            case HIDDEN:
                if (inputs.size() + outputs.size() == 0) {
                    return null;
                }
        }

        return neuronFactory.create(node, inputs, outputs);
    }

    private void initializeNeuronValues(final NeuronValueMap neuronValues, final float[] input) {
        Iterable<NodeGene> inputNodes = () -> nodes.iterator(NodeGeneType.INPUT);
        int index = 0;

        for (NodeGene inputNode : inputNodes) {
            neuronNavigator.setValueTo(neuronValues, inputNode, input[index++]);
        }
    }

    private void initializeNeuronsOrderAndValues(final NeuronValueMap neuronValues, final float[] input) {
        int index = 0;

        for (NodeGene node : nodes) {
            Neuron neuron = createNeuron(node);

            if (neuron != null) {
                neuronNavigator.add(neuron);

                if (neuron.getType() == NodeGeneType.INPUT) {
                    neuronNavigator.setValueTo(neuronValues, neuron, input[index++]);
                }
            }
        }
    }

    private void initializeNeurons(final NeuronValueMap neuronValues, final float[] input) {
        if (!neuronNavigatorInitialized.compareAndSet(false, true)) {
            while (!neuronNavigatorFinalized) {
                Thread.onSpinWait();
            }

            initializeNeuronValues(neuronValues, input);
        } else {
            initializeNeuronsOrderAndValues(neuronValues, input);
            neuronNavigatorFinalized = true;
        }
    }

    private void processNeurons(final NeuronValueMap neuronValues) {
        for (Neuron nextNeuron : neuronNavigator) {
            for (OutputNeuron outputNeuron : nextNeuron.getOutputs()) {
                neuronNavigator.addToValueTo(neuronValues, outputNeuron, nextNeuron);
            }
        }
    }

    private float[] activate(final NeuronValueMap neuronValues, final float[] input) {
        initializeNeurons(neuronValues, input);
        processNeurons(neuronValues);

        return neuronNavigator.getOutputValues(neuronValues);
    }

    @Override
    public float[] activate(final float[] input) {
        ArgumentValidatorSupport.ensureEqual(input.length, nodes.size(NodeGeneType.INPUT), "input.length");

        return activate(neuronValuesFactory.create(), input);
    }

    @Override
    public void reset() {
        neuronNavigator.clear();
        neuronNavigatorInitialized.set(false);
        neuronNavigatorFinalized = false;
    }
}
