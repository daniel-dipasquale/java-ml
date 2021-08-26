package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.sequence.SequentialId;
import com.dipasquale.data.structure.map.HashDequeMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public final class FeedForwardNeuronPathBuilder implements NeuronPathBuilder {
    private final Map<SequentialId, Neuron> neurons = new HashMap<>();
    private final Set<SequentialId> orderedNeuronIds = new HashSet<>();
    private final Collection<Neuron> orderedNeurons = new LinkedList<>();

    @Override
    public boolean hasNeurons() {
        return !neurons.isEmpty();
    }

    @Override
    public Neuron get(final SequentialId neuronId) {
        return neurons.get(neuronId);
    }

    @Override
    public Neuron add(final Neuron neuron) {
        neurons.put(neuron.getId(), neuron);

        return neuron;
    }

    private OrderableNeuron getOrderableOrCreateUnordered(final OrderableNeuron orderableNeuron, final SequentialId neuronId) {
        if (orderableNeuron != null) {
            return orderableNeuron;
        }

        Neuron neuron = neurons.get(neuronId);

        return new OrderableNeuron(neuron, false);
    }

    @Override
    public void addPathLeadingTo(final Neuron neuron) { // inspired from http://sergebg.blogspot.com/2014/11/non-recursive-dfs-topological-sort.html
        HashDequeMap<SequentialId, OrderableNeuron> deque = new HashDequeMap<>();

        deque.putLast(neuron.getId(), new OrderableNeuron(neuron, false));

        while (!deque.isEmpty()) {
            OrderableNeuron orderableNeuron = deque.removeLast();

            if (!orderableNeuron.ordered) {
                deque.putLast(orderableNeuron.neuron.getId(), orderableNeuron.createOrdered());

                for (InputNeuron inputNeuron : orderableNeuron.neuron.getInputs()) {
                    SequentialId inputNeuronId = inputNeuron.getNeuronId();
                    OrderableNeuron orderableInputNeuron = deque.get(inputNeuronId);

                    if ((orderableInputNeuron == null || !orderableInputNeuron.ordered) && !orderedNeuronIds.contains(inputNeuronId)) {
                        deque.putLast(inputNeuronId, getOrderableOrCreateUnordered(orderableInputNeuron, inputNeuronId));
                    }
                }
            } else if (orderedNeuronIds.add(orderableNeuron.neuron.getId())) {
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
    private static final class OrderableNeuron {
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
