package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.ai.rl.neat.genotype.NodeGene;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NeuronNavigator implements Iterable<Neuron> {
    private final NeuronPathBuilder neuronPathBuilder;
    private final List<Neuron> outputNeurons = new ArrayList<>();
    private Iterable<Neuron> orderedNeurons = null;

    public boolean isEmpty() {
        return !neuronPathBuilder.hasNeurons();
    }

    public Neuron get(final SequentialId id) {
        return neuronPathBuilder.get(id);
    }

    public void setValueTo(final NeuronValueMap neuronValues, final Neuron neuron, final float value) {
        neuronValues.setValue(neuron.getId(), value);
    }

    public void setValueTo(final NeuronValueMap neuronValues, final NodeGene node, final float value) {
        neuronValues.setValue(node.getId(), value);
    }

    public void add(final Neuron neuron) {
        Neuron neuronFixed = neuronPathBuilder.add(neuron);

        if (neuronFixed.getType() == NodeGeneType.OUTPUT) {
            outputNeurons.add(neuronFixed);
        }

        orderedNeurons = null;
    }

    public void addToValueTo(final NeuronValueMap neuronValues, final OutputNeuron targetNeuron, final Neuron sourceNeuron) {
        float value = sourceNeuron.getValue(neuronValues) * targetNeuron.getConnectionWeight();

        neuronValues.addToValue(targetNeuron.getNeuronId(), sourceNeuron.getId(), value);
    }

    private void ensureOrderedIsInitialized() {
        if (orderedNeurons == null) {
            for (Neuron neuron : outputNeurons) {
                neuronPathBuilder.addPathLeadingTo(neuron);
            }

            orderedNeurons = neuronPathBuilder;
        }
    }

    public float[] getOutputValues(final NeuronValueMap neuronValues) {
        float[] outputValues = new float[outputNeurons.size()];
        int index = 0;

        for (Neuron neuron : outputNeurons) {
            outputValues[index++] = neuron.getValue(neuronValues);
        }

        return outputValues;
    }

    public void clear() {
        neuronPathBuilder.clear();
        outputNeurons.clear();
        orderedNeurons = null;
    }

    @Override
    public Iterator<Neuron> iterator() {
        ensureOrderedIsInitialized();

        return orderedNeurons.iterator();
    }
}