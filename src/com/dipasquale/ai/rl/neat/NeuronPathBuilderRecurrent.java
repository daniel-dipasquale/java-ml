package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.data.structure.map.HashDequeMap;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NeuronPathBuilderRecurrent<T extends Neuron> implements NeuronPathBuilder {
    private final Map<SequentialId, NeuronStrategy<T>> neurons = new HashMap<>();
    private final NeuronPromoter<T> neuronPromoter;
    private final Set<CompositeId> alreadyOrdered = new HashSet<>();
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
        NeuronStrategy<T> neuronStrategy = new NeuronStrategy<>(neuronPromoter, neuron);

        neurons.put(neuron.getId(), neuronStrategy);

        return neuronStrategy;
    }

    @Override
    public void addPathLeadingTo(final Neuron neuron) {
        HashDequeMap<CompositeId, NeuronOrder> deque = new HashDequeMap<>();

        deque.putLast(new CompositeId(neuron.getId(), 0), new NeuronOrder(neuron, false));

        while (!deque.isEmpty()) {
            Map.Entry<CompositeId, NeuronOrder> pitstopEntry = deque.withdrawLast();

            if (!pitstopEntry.getValue().ordered) {
                deque.putLast(pitstopEntry.getKey(), new NeuronOrder(pitstopEntry.getValue().neuron, true));

                for (NeuronInput input : pitstopEntry.getValue().neuron.getInputs()) {
                    CompositeId compositeId = new CompositeId(input.getNeuronId(), pitstopEntry.getKey().cycle);
                    NeuronOrder neuronOrderOld = deque.get(compositeId);

                    if ((neuronOrderOld == null || !neuronOrderOld.ordered) && !alreadyOrdered.contains(compositeId)) {
                        NeuronOrder neuronOrderNew = Optional.ofNullable(neuronOrderOld)
                                .orElseGet(() -> new NeuronOrder(neurons.get(input.getNeuronId()), false));

                        deque.putLast(compositeId, neuronOrderNew);
                    } else if (neuronOrderOld != null && neuronOrderOld.ordered || alreadyOrdered.contains(compositeId)) {
                        if (compositeId.cycle <= input.getRecurrentCyclesAllowed()) {
                            neurons.get(input.getNeuronId()).promoteToRecurrent();
                            deque.putLast(new CompositeId(input.getNeuronId(), compositeId.cycle + 1), new NeuronOrder(neurons.get(input.getNeuronId()), false));
                        }
                    }
                }
            } else if (alreadyOrdered.add(pitstopEntry.getKey())) {
                ordered.add(pitstopEntry.getValue().neuron);
            }
        }
    }

    @Override
    public void clear() {
        neurons.clear();
        ordered.clear();
    }

    @Override
    public Iterator<Neuron> iterator() {
        return ordered.iterator();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    private static final class CompositeId {
        private final SequentialId id;
        private final int cycle;

        @Override
        public String toString() {
            return String.format("%s:%d", id, cycle);
        }
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
