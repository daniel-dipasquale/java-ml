package com.dipasquale.ai.rl.neat;

import com.dipasquale.common.CircularVersionInt;

import java.util.List;
import java.util.stream.Collectors;

final class NeuralNetworkDefault implements NeuralNetwork {
    private final GenomeDefault genome;
    private final NeuronNavigator neuronNavigator;
    private final CircularVersionInt activationNumber;
    private final NeuronFactory neuronFactory;

    NeuralNetworkDefault(final GenomeDefault genome, final NeuronPathBuilder neuronPathBuilder, final NeuronFactory neuronFactory) {
        this.genome = genome;
        this.neuronNavigator = new NeuronNavigator(neuronPathBuilder);
        this.activationNumber = new CircularVersionInt(0, Integer.MAX_VALUE);
        this.neuronFactory = neuronFactory;
    }

    private Neuron createNeuron(final NodeGene node) {
        List<NeuronInput> inputs = genome.getConnections().getIncomingToNodeFromExpressed(node).values().stream()
                .map(c -> new NeuronInput(c.getInnovationId().getSourceNodeId(), c.getRecurrentCyclesAllowed()))
                .collect(Collectors.toList());

        List<NeuronOutput> outputs = genome.getConnections().getOutgoingFromNodeFromExpressed(node).values().stream()
                .map(c -> new NeuronOutput(c.getInnovationId().getTargetNodeId(), c.getWeight()))
                .collect(Collectors.toList());

        switch (node.getType()) {
            case Bias:
            case Hidden:
                if (inputs.size() + outputs.size() == 0) {
                    return null;
                }
        }

        return neuronFactory.create(node, inputs, outputs, activationNumber);
    }

    private void initializeNeuronNavigator(final float[] input) {
        int index = 0;

        if (neuronNavigator.isEmpty()) {
            for (NodeGene node : genome.getNodes()) {
                Neuron neuron = createNeuron(node);

                if (neuron != null) {
                    neuronNavigator.add(neuron);

                    if (neuron.getType() == NodeGeneType.Input) {
                        neuron.setValue(input[index++]);
                    }
                }
            }
        } else {
            Iterable<NodeGene> inputNodes = () -> genome.getNodes().iterator(NodeGeneType.Input);

            for (NodeGene node : inputNodes) {
                neuronNavigator.get(node.getId()).setValue(input[index++]);
            }
        }
    }

    private void processNeuronsViaNavigator() {
        for (Neuron neuron : neuronNavigator) {
            for (NeuronOutput output : neuron.getOutputs()) {
                neuronNavigator.get(output.getNeuronId()).addToValue(neuron.getId(), neuron.getValue() * output.getConnectionWeight());
            }
        }
    }

    @Override
    public float[] activate(final float[] input) {
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
