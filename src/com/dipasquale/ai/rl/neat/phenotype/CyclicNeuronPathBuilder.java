package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.Id;
import com.dipasquale.data.structure.map.HashDequeMap;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
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
final class CyclicNeuronPathBuilder implements NeuronPathBuilder, Serializable {
    @Serial
    private static final long serialVersionUID = 1299761107234360133L;
    private final Map<Id, Neuron> neurons = new HashMap<>();
    private final Set<NeuronPathId> orderedNeuronIds = new HashSet<>();
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

    private static void addRootPathTo(final HashDequeMap<NeuronPathId, NeuronPath> deque, final Neuron neuron) {
        NeuronPathId neuronPathId = new NeuronPathId(neuron.getId(), 0);
        NeuronPath neuronPath = new NeuronPath(neuronPathId, neuron, true);

        deque.putLast(neuronPath.id, neuronPath);
    }

    private NeuronPath getOrCreatePath(final NeuronPath neuronPath, final NeuronPathId neuronPathId) {
        if (neuronPath != null) {
            return neuronPath;
        }

        Neuron neuron = neurons.get(neuronPathId.neuronId);

        assert neuron != null;

        return new NeuronPath(neuronPathId, neuron, false);
    }

    private NeuronPath createNextPath(final NeuronPathId neuronPathId) {
        return new NeuronPath(neuronPathId.createNext(), neurons.get(neuronPathId.neuronId), false);
    }

    @Override
    public void addPathsLeadingTo(final List<Neuron> neurons) {
        HashDequeMap<NeuronPathId, NeuronPath> orderingNeuronPaths = new HashDequeMap<>();

        for (Neuron neuron : neurons) {
            addRootPathTo(orderingNeuronPaths, neuron);
        }

        while (!orderingNeuronPaths.isEmpty()) {
            NeuronPath neuronPath = orderingNeuronPaths.removeLast();

            if (!neuronPath.ordered) {
                orderingNeuronPaths.putLast(neuronPath.id, neuronPath.flipOrdered());

                for (NeuronInputConnection connection : neuronPath.neuron.getInputConnections()) {
                    NeuronPathId sourceNeuronPathId = new NeuronPathId(connection.getSourceNeuronId(), neuronPath.id.cycle);

                    if (sourceNeuronPathId.cycle < connection.getCyclesAllowed()) {
                        NeuronPath sourceNeuronPath = orderingNeuronPaths.get(sourceNeuronPathId);

                        if ((sourceNeuronPath == null || !sourceNeuronPath.ordered) && !orderedNeuronIds.contains(sourceNeuronPathId)) {
                            NeuronPath fixedSourceNeuronPath = getOrCreatePath(sourceNeuronPath, sourceNeuronPathId);

                            orderingNeuronPaths.putLast(sourceNeuronPathId, fixedSourceNeuronPath);
                        } else if (sourceNeuronPath != null && sourceNeuronPath.ordered) {
                            NeuronPath fixedSourceNeuronPath = createNextPath(sourceNeuronPathId);

                            orderingNeuronPaths.putLast(fixedSourceNeuronPath.id, fixedSourceNeuronPath);
                        }
                    }
                }
            } else if (orderedNeuronIds.add(neuronPath.id) && !neuronPath.root) {
                orderedNeurons.add(neuronPath.neuron);
            }
        }
    }

    @Override
    public void clear() {
        neurons.clear();
        orderedNeurons.clear();
    }

    @Override
    public Iterator<Neuron> iterator() {
        return orderedNeurons.iterator();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    private static final class NeuronPathId implements Serializable {
        @Serial
        private static final long serialVersionUID = -445055006068211779L;
        private final Id neuronId;
        private final int cycle;

        private NeuronPathId createNext() {
            return new NeuronPathId(neuronId, cycle + 1);
        }

        @Override
        public String toString() {
            return String.format("%s:%d", neuronId, cycle);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class NeuronPath implements Serializable {
        @Serial
        private static final long serialVersionUID = -4219700326308862911L;
        private final NeuronPathId id;
        private final Neuron neuron;
        private boolean ordered = false;
        private final boolean root;

        public NeuronPath flipOrdered() {
            ordered = !ordered;

            return this;
        }

        @Override
        public String toString() {
            return String.format("%s:%b", id, ordered);
        }
    }
}
