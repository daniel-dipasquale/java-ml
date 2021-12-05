package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.common.Id;
import com.dipasquale.ai.rl.neat.genotype.NodeGeneType;
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

    private void ensureOrderedIsInitialized() {
        if (orderedNeurons == null) {
            for (Neuron neuron : outputNeurons) {
                neuronPathBuilder.addPathLeadingTo(neuron);
            }

            orderedNeurons = neuronPathBuilder;
        }
    }

    public float[] getOutputValues(final NeuronStateGroup neuronState) {
        float[] outputValues = new float[outputNeurons.size()];
        int index = 0;

        for (Neuron neuron : outputNeurons) {
            outputValues[index++] = neuron.getValue(neuronState);
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