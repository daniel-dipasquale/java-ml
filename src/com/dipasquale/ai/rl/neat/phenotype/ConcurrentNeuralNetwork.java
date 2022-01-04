package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.internal.Id;
import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.factory.ObjectFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

final class ConcurrentNeuralNetwork implements NeuralNetwork, Serializable {
    @Serial
    private static final long serialVersionUID = 2271165226501445902L;
    private final Genome genome;
    private final int inputSize;
    private final NeuronNavigator neuronNavigator;
    private final AtomicBoolean neuronNavigatorInitialized;
    private volatile boolean neuronNavigatorFinalized;
    private final ObjectFactory<NeuronMemory> neuronMemoryFactory;
    private final NeuronStateGroupFactory neuronStateFactory;

    ConcurrentNeuralNetwork(final Genome genome, final NeuronPathBuilder neuronPathBuilder, final ObjectFactory<NeuronMemory> neuronMemoryFactory, final NeuronStateGroupFactory neuronStateFactory) {
        this.genome = genome;
        this.inputSize = genome.getNodes().size(NodeGeneType.INPUT);
        this.neuronNavigator = new NeuronNavigator(neuronPathBuilder);
        this.neuronNavigatorInitialized = new AtomicBoolean(false);
        this.neuronNavigatorFinalized = false;
        this.neuronMemoryFactory = neuronMemoryFactory;
        this.neuronStateFactory = neuronStateFactory;
    }

    @Override
    public NeuronMemory createMemory() {
        return neuronMemoryFactory.create();
    }

    private Neuron createNeuron(final NodeGene node) {
        List<NeuronInputConnection> inputConnections = genome.getConnections().getExpressed().getIncomingToNode(node).values().stream()
                .map(c -> new NeuronInputConnection(c.getInnovationId().getSourceNodeId(), c.getCyclesAllowed()))
                .collect(Collectors.toList());

        List<NeuronOutputConnection> outputConnections = genome.getConnections().getExpressed().getOutgoingFromNode(node).values().stream()
                .map(c -> new NeuronOutputConnection(c.getInnovationId().getTargetNodeId(), c.getWeight(), c.getRecurrentWeights()))
                .collect(Collectors.toList());

        switch (node.getType()) {
            case BIAS:
            case HIDDEN:
                if (inputConnections.size() + outputConnections.size() == 0) {
                    return null;
                }
        }

        return new Neuron(node, inputConnections, outputConnections);
    }

    private void initializeNeuronValues(final NeuronStateGroup neuronState, final float[] input) {
        Iterable<NodeGene> inputNodes = () -> genome.getNodes().iterator(NodeGeneType.INPUT);
        int index = 0;

        for (NodeGene inputNode : inputNodes) {
            neuronState.setValue(inputNode.getId(), input[index++]);
        }
    }

    private void setNeuronInvocationOrderAndInitialValues(final NeuronStateGroup neuronState, final float[] input) {
        int index = 0;

        for (NodeGene node : genome.getNodes()) {
            Neuron neuron = createNeuron(node);

            if (neuron != null) {
                neuronNavigator.add(neuron);

                if (neuron.getType() == NodeGeneType.INPUT) {
                    neuronState.setValue(neuron.getId(), input[index++]);
                }
            }
        }
    }

    private void initializeNeurons(final NeuronStateGroup neuronState, final float[] input) {
        if (!neuronNavigatorInitialized.compareAndSet(false, true)) {
            while (!neuronNavigatorFinalized) {
                Thread.onSpinWait();
            }

            initializeNeuronValues(neuronState, input);
        } else {
            setNeuronInvocationOrderAndInitialValues(neuronState, input);
            neuronNavigatorFinalized = true;
        }
    }

    private void processNeurons(final NeuronStateGroup neuronState) {
        for (Neuron neuron : neuronNavigator) {
            Id neuronId = neuron.getId();

            for (NeuronOutputConnection connection : neuron.getOutputConnections()) {
                float value = neuronState.calculateValue(neuron, connection);

                neuronState.addValue(connection.getTargetNeuronId(), value, neuronId);
            }

            neuronState.endCycle(neuronId);
        }
    }

    private float[] activate(final NeuronStateGroup neuronState, final float[] input) {
        initializeNeurons(neuronState, input);
        processNeurons(neuronState);

        return neuronNavigator.getOutputValues(neuronState);
    }

    @Override
    public float[] activate(final float[] input, final NeuronMemory neuronMemory) {
        ArgumentValidatorSupport.ensureEqual(input.length, inputSize, "input.length");
        ArgumentValidatorSupport.ensureTrue(neuronMemory == null || neuronMemory.isOwnedBy(genome), "neuronMemory", "does not belong to the genome");

        return activate(neuronStateFactory.create(neuronMemory), input);
    }

    public void reset() {
        neuronNavigator.clear();
        neuronNavigatorInitialized.set(false);
        neuronNavigatorFinalized = false;
    }
}
