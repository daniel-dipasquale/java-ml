package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.internal.Id;
import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.concurrent.AtomicLazyReference;
import com.dipasquale.common.factory.ObjectFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

final class ConcurrentNeuralNetwork implements NeuralNetwork, Serializable {
    @Serial
    private static final long serialVersionUID = 2271165226501445902L;
    private final Genome genome;
    private final int inputSize;
    private final AtomicLazyReference<NeuronNavigator> neuronNavigatorProvider;
    private final ObjectFactory<NeuronMemory> neuronMemoryFactory;
    private final NeuronStateGroupFactory neuronStateFactory;

    ConcurrentNeuralNetwork(final Genome genome, final NeuronPathBuilder neuronPathBuilder, final NeuronLayerNormalizer outputLayerNormalizer, final ObjectFactory<NeuronMemory> neuronMemoryFactory, final NeuronStateGroupFactory neuronStateFactory) {
        this.genome = genome;
        this.inputSize = genome.getNodes().size(NodeGeneType.INPUT);
        this.neuronNavigatorProvider = new AtomicLazyReference<>((ObjectFactory<NeuronNavigator> & Serializable) () -> createNeuronNavigator(genome, neuronPathBuilder, outputLayerNormalizer));
        this.neuronMemoryFactory = neuronMemoryFactory;
        this.neuronStateFactory = neuronStateFactory;
    }

    private static NeuronNavigator createNeuronNavigator(final Genome genome, final NeuronPathBuilder neuronPathBuilder, final NeuronLayerNormalizer outputLayerNormalizer) {
        NeuronNavigator neuronNavigator = new NeuronNavigator(neuronPathBuilder, outputLayerNormalizer);

        for (NodeGene node : genome.getNodes()) {
            Neuron neuron = createNeuronIfApplicable(genome, node);

            if (neuron != null) {
                neuronNavigator.add(neuron);
            }
        }

        neuronNavigator.build();

        return neuronNavigator;
    }

    @Override
    public NeuronMemory createMemory() {
        return neuronMemoryFactory.create();
    }

    private static Neuron createNeuronIfApplicable(final Genome genome, final NodeGene node) {
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

    private void initializeNeurons(final NeuronStateGroup neuronState, final float[] input) {
        Iterable<NodeGene> inputNodes = () -> genome.getNodes().iterator(NodeGeneType.INPUT);
        int index = 0;

        for (NodeGene inputNode : inputNodes) {
            neuronState.setValue(inputNode.getId(), input[index++]);
        }
    }

    private static void processNeurons(final NeuronNavigator neuronNavigator, final NeuronStateGroup neuronState) {
        for (Neuron neuron : neuronNavigator) {
            Id neuronId = neuron.getId();

            for (NeuronOutputConnection connection : neuron.getOutputConnections()) {
                float value = neuronState.calculateValue(neuron, connection);

                neuronState.addValue(connection.getTargetNeuronId(), value, neuronId);
            }

            neuronState.endCycle(neuronId);
        }
    }

    private float[] activate(final NeuronMemory neuronMemory, final float[] input) {
        NeuronNavigator neuronNavigator = neuronNavigatorProvider.reference();
        NeuronStateGroup neuronState = neuronStateFactory.create(neuronMemory);

        initializeNeurons(neuronState, input);
        processNeurons(neuronNavigator, neuronState);

        return neuronNavigator.getOutputValues(neuronState);
    }

    @Override
    public float[] activate(final float[] input, final NeuronMemory neuronMemory) {
        ArgumentValidatorSupport.ensureEqual(input.length, inputSize, "input.length");
        ArgumentValidatorSupport.ensureTrue(neuronMemory == null || neuronMemory.isOwnedBy(genome), "neuronMemory", "does not belong to the genome");

        return activate(neuronMemory, input);
    }
}
