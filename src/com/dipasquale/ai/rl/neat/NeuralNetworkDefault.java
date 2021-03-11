package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.common.CircularVersionInt;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NeuralNetworkDefault implements NeuralNetwork {
    private final GenomeDefault genome;
    private final NeuronNavigator<NeuronDefault> neuronNavigator = new NeuronNavigator<>(NeuronDefault::createRecurrentSingleMemory);
    private final CircularVersionInt activationNumber = new CircularVersionInt(0, Integer.MAX_VALUE);

    private Neuron createNeuron(final NodeGene node) {
        Set<SequentialId> inputIds = genome.getConnections().getIncomingToNodeFromExpressed(node).keySet().stream()
                .map(DirectedEdge::getSourceNodeId)
                .collect(Collectors.toSet());

        List<NeuronOutput> outputs = genome.getConnections().getOutgoingFromNodeFromExpressed(node).values().stream()
                .map(c -> new NeuronOutput(c.getInnovationId().getTargetNodeId(), c.getWeight(), c.getRecurrentCyclesAllowed()))
                .collect(Collectors.toList());

        switch (node.getType()) {
            case Bias:
            case Hidden:
                if (inputIds.size() + outputs.size() == 0) {
                    return null;
                }
        }

        return new NeuronDefault(node, inputIds, outputs, activationNumber);
    }

    private void initializeNeuronNavigator(final float[] input) {
        int index = 0;

        if (neuronNavigator.isEmpty()) {
            for (NodeGene node : genome.getNodes()) {
                Neuron neuron = createNeuron(node);

                if (neuron != null) {
                    neuronNavigator.add(neuron);

                    if (neuron.getType() == NodeGeneType.Input) {
                        neuron.forceValue(input[index++]);
                    }
                }
            }
        } else {
            Iterable<NodeGene> inputNodes = () -> genome.getNodes().iterator(NodeGeneType.Input);

            for (NodeGene node : inputNodes) {
                neuronNavigator.get(node.getId()).forceValue(input[index++]);
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
