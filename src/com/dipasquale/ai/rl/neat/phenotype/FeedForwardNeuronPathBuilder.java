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
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public final class FeedForwardNeuronPathBuilder implements NeuronPathBuilder {
    private final Map<SequentialId, Neuron> neurons = new HashMap<>();
    private final Set<SequentialId> alreadyOrdered = new HashSet<>();
    private final Collection<Neuron> ordered = new LinkedList<>();

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

    @Override
    public void addPathLeadingTo(final Neuron neuron) { // inspired from http://sergebg.blogspot.com/2014/11/non-recursive-dfs-topological-sort.html
        HashDequeMap<SequentialId, NeuronOrder> deque = new HashDequeMap<>();

        deque.putLast(neuron.getId(), new NeuronOrder(neuron, false));

        while (!deque.isEmpty()) {
            NeuronOrder neuronOrder = deque.removeLast();

            if (!neuronOrder.ordered) {
                deque.putLast(neuronOrder.neuron.getId(), new NeuronOrder(neuronOrder.neuron, true));

                for (InputNeuron input : neuronOrder.neuron.getInputs()) {
                    NeuronOrder neuronOrderOld = deque.get(input.getNeuronId());

                    if ((neuronOrderOld == null || !neuronOrderOld.ordered) && !alreadyOrdered.contains(input.getNeuronId())) {
                        NeuronOrder neuronOrderNew = Optional.ofNullable(neuronOrderOld)
                                .orElseGet(() -> new NeuronOrder(neurons.get(input.getNeuronId()), false));

                        deque.putLast(input.getNeuronId(), neuronOrderNew);
                    }
                }
            } else if (alreadyOrdered.add(neuronOrder.neuron.getId())) {
                ordered.add(neuronOrder.neuron);
            }
        }
    }

    @Override
    public void clear() {
        neurons.clear();
        alreadyOrdered.clear();
        ordered.clear();
    }

    @Override
    public Iterator<Neuron> iterator() {
        return ordered.iterator();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class NeuronOrder {
        private final Neuron neuron;
        private final boolean ordered;

        @Override
        public String toString() {
            return String.format("%s:%b", neuron.getId(), ordered);
        }
    }
}
