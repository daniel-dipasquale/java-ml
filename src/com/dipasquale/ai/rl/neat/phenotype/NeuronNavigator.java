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
    private final NeuronLayerTopologyDefinition neuronLayerTopologyDefinition;

    public boolean isEmpty() {
        return !neuronPathBuilder.hasNeurons();
    }

    public Neuron get(final Id id) {
        return neuronPathBuilder.get(id);
    }

    public void add(final Neuron neuron) {
        if (orderedNeurons != null) {
            orderedNeurons = null;
        }

        neuronPathBuilder.add(neuron);

        if (neuron.getType() == NodeGeneType.OUTPUT) {
            outputNeurons.add(neuron);
        }

        orderedNeurons = null;
    }

    public void build() {
        if (orderedNeurons != null) {
            return;
        }

        neuronPathBuilder.addPathsLeadingTo(outputNeurons);
        orderedNeurons = neuronPathBuilder;
    }

    public float[] getOutputValues(final NeuronStateGroup neuronState) {
        NeuronLayerReader reader = new NeuronLayerReader(outputNeurons, neuronState);

        return neuronLayerTopologyDefinition.getValues(reader);
    }

    public void clear() {
        neuronPathBuilder.clear();
        outputNeurons.clear();
        orderedNeurons = null;
    }

    @Override
    public Iterator<Neuron> iterator() {
        build();

        return orderedNeurons.iterator();
    }
}