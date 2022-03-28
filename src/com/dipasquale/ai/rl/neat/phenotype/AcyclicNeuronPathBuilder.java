package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.internal.Id;
import com.dipasquale.data.structure.map.HashDequeMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class AcyclicNeuronPathBuilder implements NeuronPathBuilder, Serializable {
    @Serial
    private static final long serialVersionUID = -9211062291079454674L;
    private final Map<Id, Neuron> neurons = new HashMap<>();
    private final Set<Id> orderedNeuronIds = new HashSet<>();
    private final Collection<Neuron> orderedNeurons = new LinkedList<>();

    @Override
    public boolean hasNeurons() {
        return !neurons.isEmpty();
    }

    @Override
    public Neuron get(final Id neuronId) {
        return neurons.get(neuronId);
    }

    @Override
    public void add(final Neuron neuron) {
        neurons.put(neuron.getId(), neuron);
    }

    private NeuronPath getOrCreatePath(final NeuronPath neuronPath, final Id neuronId) {
        if (neuronPath != null) {
            return neuronPath;
        }

        Neuron neuron = neurons.get(neuronId);

        return new NeuronPath(neuron, false);
    }

    @Override
    public void addPathsLeadingTo(final List<Neuron> neurons) { // inspired from http://sergebg.blogspot.com/2014/11/non-recursive-dfs-topological-sort.html
        HashDequeMap<Id, NeuronPath> orderingNeuronPaths = new HashDequeMap<>();

        for (Neuron neuron : neurons) {
            orderingNeuronPaths.putLast(neuron.getId(), new NeuronPath(neuron, true));
        }

        while (!orderingNeuronPaths.isEmpty()) {
            NeuronPath neuronPath = orderingNeuronPaths.removeLast();

            if (!neuronPath.ordered) {
                orderingNeuronPaths.putLast(neuronPath.neuron.getId(), neuronPath.flipOrdered());

                for (NeuronInputConnection connection : neuronPath.neuron.getInputConnections()) {
                    Id sourceId = connection.getSourceNeuronId();
                    NeuronPath sourceNeuronPath = orderingNeuronPaths.get(sourceId);

                    if ((sourceNeuronPath == null || !sourceNeuronPath.ordered) && !orderedNeuronIds.contains(sourceId)) {
                        NeuronPath fixedSourceNeuronPath = getOrCreatePath(sourceNeuronPath, sourceId);

                        orderingNeuronPaths.putLast(sourceId, fixedSourceNeuronPath);
                    }
                }
            } else if (orderedNeuronIds.add(neuronPath.neuron.getId()) && !neuronPath.root) {
                orderedNeurons.add(neuronPath.neuron);
            }
        }
    }

    @Override
    public void clear() {
        neurons.clear();
        orderedNeuronIds.clear();
        orderedNeurons.clear();
    }

    @Override
    public Iterator<Neuron> iterator() {
        return orderedNeurons.iterator();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class NeuronPath implements Serializable {
        @Serial
        private static final long serialVersionUID = -6397213033769512814L;
        private final Neuron neuron;
        private boolean ordered = false;
        private final boolean root;

        public NeuronPath flipOrdered() {
            ordered = !ordered;

            return this;
        }

        @Override
        public String toString() {
            return String.format("%s:%b", neuron.getId(), ordered);
        }
    }
}
