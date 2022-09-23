package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.Id;
import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.concurrent.AtomicLazyReference;
import com.dipasquale.common.factory.ObjectFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

final class ConcurrentNeatNeuralNetwork implements NeatNeuralNetwork, Serializable {
    @Serial
    private static final long serialVersionUID = 2271165226501445902L;
    private final Genome genome;
    private final int inputSize;
    private final AtomicLazyReference<NeuronNavigator> neuronNavigatorProvider;
    private final ObjectFactory<NeatNeuronMemory> neuronMemoryFactory;
    private final NeuronStateGroupFactory neuronStateFactory;

    private static Neuron createNeuronIfValid(final Genome genome, final NodeGene nodeGene) {
        List<NeuronInputConnection> inputConnections = genome.getConnectionGenes().getExpressed().getIncomingToNodeGene(nodeGene).values().stream()
                .map(connectionGene -> new NeuronInputConnection(connectionGene.getInnovationId().getSourceNodeGeneId(), connectionGene.getCyclesAllowed()))
                .toList();

        List<NeuronOutputConnection> outputConnections = genome.getConnectionGenes().getExpressed().getOutgoingFromNodeGene(nodeGene).values().stream()
                .map(connectionGene -> new NeuronOutputConnection(connectionGene.getInnovationId().getTargetNodeGeneId(), connectionGene.getWeight(), connectionGene.getRecurrentWeights()))
                .toList();

        switch (nodeGene.getType()) {
            case BIAS:
            case HIDDEN:
                if (inputConnections.size() + outputConnections.size() == 0) {
                    return null;
                }
        }

        return new Neuron(nodeGene, inputConnections, outputConnections);
    }

    private static NeuronNavigator createNeuronNavigator(final Genome genome, final NeuronPathBuilder neuronPathBuilder, final NeuronLayerTopologyDefinition outputTopologyDefinition) {
        NeuronNavigator neuronNavigator = new NeuronNavigator(neuronPathBuilder, outputTopologyDefinition);

        for (NodeGene nodeGene : genome.getNodeGenes()) {
            Neuron neuron = createNeuronIfValid(genome, nodeGene);

            if (neuron != null) {
                neuronNavigator.add(neuron);
            }
        }

        neuronNavigator.build();

        return neuronNavigator;
    }

    ConcurrentNeatNeuralNetwork(final Genome genome, final NeuronPathBuilder neuronPathBuilder, final NeuronLayerTopologyDefinition outputTopologyDefinition, final ObjectFactory<NeatNeuronMemory> neuronMemoryFactory, final NeuronStateGroupFactory neuronStateFactory) {
        this.genome = genome;
        this.inputSize = genome.getNodeGenes().size(NodeGeneType.INPUT);
        this.neuronNavigatorProvider = new AtomicLazyReference<>((ObjectFactory<NeuronNavigator> & Serializable) () -> createNeuronNavigator(genome, neuronPathBuilder, outputTopologyDefinition));
        this.neuronMemoryFactory = neuronMemoryFactory;
        this.neuronStateFactory = neuronStateFactory;
    }

    @Override
    public NeatNeuronMemory createMemory() {
        return neuronMemoryFactory.create();
    }

    private void initializeNeurons(final NeuronStateGroup neuronState, final float[] input) {
        Iterable<NodeGene> inputNodeGenes = () -> genome.getNodeGenes().iterator(NodeGeneType.INPUT);
        int index = 0;

        for (NodeGene inputNodeGene : inputNodeGenes) {
            neuronState.setValue(inputNodeGene.getId(), input[index++]);
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

    private float[] activate(final NeatNeuronMemory neuronMemory, final float[] input) {
        NeuronNavigator neuronNavigator = neuronNavigatorProvider.getReference();
        NeuronStateGroup neuronState = neuronStateFactory.create(neuronMemory);

        initializeNeurons(neuronState, input);
        processNeurons(neuronNavigator, neuronState);

        return neuronNavigator.getOutputValues(neuronState);
    }

    @Override
    public float[] activate(final float[] input, final NeatNeuronMemory neuronMemory) {
        ArgumentValidatorSupport.ensureEqual(input.length, inputSize, "input.length");
        ArgumentValidatorSupport.ensureTrue(neuronMemory == null || neuronMemory.isOwnedBy(genome), "neuronMemory", "does not belong to the genome");

        return activate(neuronMemory, input);
    }
}
