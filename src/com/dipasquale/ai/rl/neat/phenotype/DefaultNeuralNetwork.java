package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.ConnectionGeneMap;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneMap;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.CyclicVersion;

import java.util.List;
import java.util.stream.Collectors;

public final class DefaultNeuralNetwork implements NeuralNetwork {
    private final NodeGeneMap nodes;
    private final ConnectionGeneMap connections;
    private final NeuronNavigator neuronNavigator;
    private final CyclicVersion activationNumber;
    private final NeuronFactory neuronFactory;

    public DefaultNeuralNetwork(final NodeGeneMap nodes, final ConnectionGeneMap connections, final NeuronPathBuilder neuronPathBuilder, final NeuronFactory neuronFactory) {
        this.nodes = nodes;
        this.connections = connections;
        this.neuronNavigator = new NeuronNavigator(neuronPathBuilder);
        this.activationNumber = new CyclicVersion(0, Integer.MAX_VALUE);
        this.neuronFactory = neuronFactory;
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

        return neuronFactory.create(node, inputs, outputs, activationNumber);
    }

    private void initializeNeuronNavigator(final float[] input) {
        int index = 0;

        if (neuronNavigator.isEmpty()) {
            for (NodeGene node : nodes) {
                Neuron neuron = createNeuron(node);

                if (neuron != null) {
                    neuronNavigator.add(neuron);

                    if (neuron.getType() == NodeGeneType.INPUT) {
                        neuron.setValue(input[index++]);
                    }
                }
            }
        } else {
            Iterable<NodeGene> inputNodes = () -> nodes.iterator(NodeGeneType.INPUT);

            for (NodeGene node : inputNodes) {
                neuronNavigator.get(node.getId()).setValue(input[index++]);
            }
        }
    }

    private void processNeuronsViaNavigator() {
        for (Neuron neuron : neuronNavigator) {
            for (OutputNeuron output : neuron.getOutputs()) {
                neuronNavigator.get(output.getNeuronId()).addToValue(neuron.getId(), neuron.getValue() * output.getConnectionWeight());
            }
        }
    }

    @Override
    public float[] activate(final float[] input) {
        ArgumentValidatorSupport.ensureEqual(input.length, nodes.size(NodeGeneType.INPUT), "input.length");
        activationNumber.next();
        initializeNeuronNavigator(input);
        processNeuronsViaNavigator();

        return neuronNavigator.getOutputValues();
    }

    @Override
    public void reset() {
        neuronNavigator.clear();
    }
}
