package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NeuralNetworkFeedForward<T extends Comparable<T>> implements NeuralNetwork {
    private final GenomeDefault<T> genome;
    private final NeuronNavigator<T> neuronNavigator = new NeuronNavigator<>();

    private Neuron<T> createNeuron(final NodeGene<T> node) {
        Set<T> inputIds = genome.getConnections().getIncomingToNodeFromExpressed(node).keySet().stream()
                .map(DirectedEdge::getSourceNodeId)
                .collect(Collectors.toSet());

        List<Neuron.Output<T>> outputs = genome.getConnections().getOutgoingFromNodeFromExpressed(node).values().stream()
                .map(c -> new Neuron.Output<>(c.getInnovationId().getTargetNodeId(), c.getWeight()))
                .collect(Collectors.toList());

        switch (node.getType()) {
            case Bias:
            case Hidden:
                if (inputIds.size() + outputs.size() == 0) {
                    return null;
                }
        }

        return new NeuronFeedForward<>(node, inputIds, outputs);
    }

    private void initializeNeuronNavigator(final float[] input) {
        int index = 0;

        if (neuronNavigator.isEmpty()) {
            for (NodeGene<T> node : genome.getNodes()) {
                Neuron<T> neuron = createNeuron(node);

                if (neuron != null) {
                    neuronNavigator.add(neuron);

                    if (node.getType() == NodeGene.Type.Input) {
                        neuronNavigator.setValue(node, input[index++]);
                    }
                }
            }
        } else {
            Iterable<NodeGene<T>> inputNodes = () -> genome.getNodes().iterator(NodeGene.Type.Input);

            for (NodeGene<T> node : inputNodes) {
                neuronNavigator.setValue(node, input[index++]);
            }
        }
    }

    private void processNeuronsViaNavigator() {
        for (Neuron<T> neuron : neuronNavigator) {
            for (Neuron.Output<T> output : neuron.getOutputs()) {
                neuron.addToValue(output.getNeuronId(), neuronNavigator.get(output.getNeuronId()).getValue() * output.getConnectionWeight());
            }
        }
    }

    @Override
    public float[] activate(final float[] input) {
        initializeNeuronNavigator(input);
        processNeuronsViaNavigator();

        return neuronNavigator.getOutputValues();
    }

    @Override
    public void reset() {
        neuronNavigator.clear();
    }
}

/*
if (context.connections().allowCyclicConnections()) {
    Set<T> outputNodeIds = outputs.stream()
            .map(Neuron.Output::getId)
            .collect(Collectors.toSet());

    Set<T> mandatoryInputIds = genome.getConnections().getIncomingToNodeFromExpressed(node).keySet().stream()
            .map(DirectedEdge::getSourceNodeId)
            .filter(nid -> !outputNodeIds.contains(nid))
            .collect(Collectors.toSet());

    return new Neuron<>(node, mandatoryInputIds, outputs);
}
 */