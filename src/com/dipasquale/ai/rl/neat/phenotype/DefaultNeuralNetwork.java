package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.Genome;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.factory.ObjectFactory;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public final class DefaultNeuralNetwork implements NeuralNetwork, Serializable {
    @Serial
    private static final long serialVersionUID = 2271165226501445902L;
    private final Genome genome;
    private final NeuronNavigator neuronNavigator;
    private final AtomicBoolean neuronNavigatorInitialized;
    private volatile boolean neuronNavigatorFinalized;
    private final ObjectFactory<NeuronValueGroup> neuronValuesFactory;

    public DefaultNeuralNetwork(final Genome genome, final NeuronPathBuilder neuronPathBuilder, final ObjectFactory<NeuronValueGroup> neuronValuesFactory) {
        this.genome = genome;
        this.neuronNavigator = new NeuronNavigator(neuronPathBuilder);
        this.neuronNavigatorInitialized = new AtomicBoolean(false);
        this.neuronNavigatorFinalized = false;
        this.neuronValuesFactory = neuronValuesFactory;
    }

    private Neuron createNeuron(final NodeGene node) {
        List<InputNeuron> inputs = genome.getConnections().getExpressed().getIncomingToNode(node).values().stream()
                .map(c -> new InputNeuron(c.getInnovationId().getSourceNodeId(), c.getRecurrentCyclesAllowed()))
                .collect(Collectors.toList());

        List<OutputNeuron> outputs = genome.getConnections().getExpressed().getOutgoingFromNode(node).values().stream()
                .map(c -> new OutputNeuron(c.getInnovationId().getTargetNodeId(), c.getWeight()))
                .collect(Collectors.toList());

        switch (node.getType()) {
            case BIAS:
            case HIDDEN:
                if (inputs.size() + outputs.size() == 0) {
                    return null;
                }
        }

        return new Neuron(node, inputs, outputs);
    }

    private static void setValueTo(final NeuronValueGroup neuronValues, final NodeGene node, final float value) {
        neuronValues.setValue(node.getId(), value);
    }

    private Iterable<NodeGene> getInputNodes() {
        return () -> genome.getNodes().iterator(NodeGeneType.INPUT);
    }

    private void initializeNeuronValues(final NeuronValueGroup neuronValues, final float[] input) {
        int index = 0;

        for (NodeGene inputNode : getInputNodes()) {
            setValueTo(neuronValues, inputNode, input[index++]);
        }
    }

    private static void setValueTo(final NeuronValueGroup neuronValues, final Neuron neuron, final float value) {
        neuronValues.setValue(neuron.getId(), value);
    }

    private void initializeNeuronsOrderAndValues(final NeuronValueGroup neuronValues, final float[] input) {
        int index = 0;

        for (NodeGene node : genome.getNodes()) {
            Neuron neuron = createNeuron(node);

            if (neuron != null) {
                neuronNavigator.add(neuron);

                if (neuron.getType() == NodeGeneType.INPUT) {
                    setValueTo(neuronValues, neuron, input[index++]);
                }
            }
        }
    }

    private void initializeNeurons(final NeuronValueGroup neuronValues, final float[] input) {
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

    private static void addToValueTo(final NeuronValueGroup neuronValues, final OutputNeuron targetNeuron, final Neuron sourceNeuron) {
        float value = sourceNeuron.getValue(neuronValues) * targetNeuron.getConnectionWeight();

        neuronValues.addToValue(targetNeuron.getNeuronId(), value, sourceNeuron.getId());
    }

    private void processNeurons(final NeuronValueGroup neuronValues) {
        for (Neuron nextNeuron : neuronNavigator) {
            for (OutputNeuron outputNeuron : nextNeuron.getOutputs()) {
                addToValueTo(neuronValues, outputNeuron, nextNeuron);
            }
        }
    }

    private float[] activate(final NeuronValueGroup neuronValues, final float[] input) {
        initializeNeurons(neuronValues, input);
        processNeurons(neuronValues);

        return neuronNavigator.getOutputValues(neuronValues);
    }

    @Override
    public float[] activate(final float[] input) {
        ArgumentValidatorSupport.ensureEqual(input.length, genome.getNodes().size(NodeGeneType.INPUT), "input.length");

        return activate(neuronValuesFactory.create(), input);
    }

    public void reset() {
        neuronNavigator.clear();
        neuronNavigatorInitialized.set(false);
        neuronNavigatorFinalized = false;
    }
}
