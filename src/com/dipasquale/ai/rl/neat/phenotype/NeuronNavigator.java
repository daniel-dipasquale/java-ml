package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
import com.dipasquale.ai.rl.neat.internal.Id;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NeuronNavigator implements Iterable<Neuron>, Serializable {
    @Serial
    private static final long serialVersionUID = 4765089484534330586L;
    private final NeuronPathBuilder neuronPathBuilder;
    private final List<Neuron> outputNeurons = new ArrayList<>();
    private Iterable<Neuron> orderedNeurons = null;

    public boolean isEmpty() {
        return !neuronPathBuilder.hasNeurons();
    }

    public Neuron get(final Id id) {
        return neuronPathBuilder.get(id);
    }

    public void add(final Neuron neuron) {
        neuronPathBuilder.add(neuron);

        if (neuron.getType() == NodeGeneType.OUTPUT) {
            outputNeurons.add(neuron);
        }

        orderedNeurons = null;
    }

    private void ensureOrderedNeuronsIsInitialized() {
        if (orderedNeurons != null) {
            return;
        }

        neuronPathBuilder.addPathsLeadingTo(outputNeurons);
        orderedNeurons = neuronPathBuilder;
    }

    public float[] getOutputValues(final NeuronStateGroup neuronState) {
        float[] outputValues = new float[outputNeurons.size()];

        for (int i = 0; i < outputValues.length; i++) {
            Neuron outputNeuron = outputNeurons.get(i);

            outputValues[i] = neuronState.calculateValue(outputNeuron);
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
        ensureOrderedNeuronsIsInitialized();

        return orderedNeurons.iterator();
    }
}