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

    private OrderableNeuron getOrderableOrCreateUnordered(final OrderableNeuron orderableNeuron, final Id neuronId) {
        if (orderableNeuron != null) {
            return orderableNeuron;
        }

        Neuron neuron = neurons.get(neuronId);

        return new OrderableNeuron(neuron, false);
    }

    @Override
    public void addPathLeadingTo(final Neuron neuron) { // inspired from http://sergebg.blogspot.com/2014/11/non-recursive-dfs-topological-sort.html
        HashDequeMap<Id, OrderableNeuron> orderingNeurons = new HashDequeMap<>();

        orderingNeurons.putLast(neuron.getId(), new OrderableNeuron(neuron, false));

        while (!orderingNeurons.isEmpty()) {
            OrderableNeuron orderableNeuron = orderingNeurons.removeLast();

            if (!orderableNeuron.ordered) {
                orderingNeurons.putLast(orderableNeuron.neuron.getId(), orderableNeuron.createOrdered());

                for (NeuronInputConnection connection : orderableNeuron.neuron.getInputConnections()) {
                    Id sourceNeuronId = connection.getSourceNeuronId();
                    OrderableNeuron orderableSourceNeuron = orderingNeurons.get(sourceNeuronId);

                    if ((orderableSourceNeuron == null || !orderableSourceNeuron.ordered) && !orderedNeuronIds.contains(sourceNeuronId)) {
                        orderingNeurons.putLast(sourceNeuronId, getOrderableOrCreateUnordered(orderableSourceNeuron, sourceNeuronId));
                    }
                }
            } else if (orderedNeuronIds.add(orderableNeuron.neuron.getId()) && !orderingNeurons.isEmpty()) {
                orderedNeurons.add(orderableNeuron.neuron);
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
    private static final class OrderableNeuron implements Serializable {
        @Serial
        private static final long serialVersionUID = -6397213033769512814L;
        private final Neuron neuron;
        private final boolean ordered;

        public OrderableNeuron createOrdered() {
            return new OrderableNeuron(neuron, true);
        }

        @Override
        public String toString() {
            return String.format("%s:%b", neuron.getId(), ordered);
        }
    }
}
