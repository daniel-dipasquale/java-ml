package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.common.sequence.SequentialId;
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
import java.util.Set;

@RequiredArgsConstructor
public final class RecurrentNeuronPathBuilder implements NeuronPathBuilder {
    private final Map<SequentialId, Neuron> neurons = new HashMap<>();
    private final Set<NeuronOrderId> orderedNeuronIds = new HashSet<>();
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

    private OrderableNeuron getOrderableOrCreateUnordered(final OrderableNeuron orderableNeuron, final NeuronOrderId neuronOrderId) {
        if (orderableNeuron != null) {
            return orderableNeuron;
        }

        Neuron neuron = neurons.get(neuronOrderId.neuronId);

        return new OrderableNeuron(neuronOrderId, neuron, false);
    }

    private static OrderableNeuron createUnordered(final Neuron neuron) {
        NeuronOrderId neuronOrderId = new NeuronOrderId(neuron.getId(), 0);

        return new OrderableNeuron(neuronOrderId, neuron, false);
    }

    private static void addRootTo(final HashDequeMap<NeuronOrderId, OrderableNeuron> deque, final Neuron neuron) {
        OrderableNeuron orderableNeuron = createUnordered(neuron);

        deque.putLast(orderableNeuron.neuronOrderId, orderableNeuron);
    }

    private OrderableNeuron createNextUnordered(final NeuronOrderId neuronOrderId) {
        return new OrderableNeuron(neuronOrderId.createNext(), neurons.get(neuronOrderId.neuronId), false);
    }

    @Override
    public void addPathLeadingTo(final Neuron neuron) {
        HashDequeMap<NeuronOrderId, OrderableNeuron> deque = new HashDequeMap<>();

        addRootTo(deque, neuron);

        while (!deque.isEmpty()) {
            OrderableNeuron orderableNeuron = deque.removeLast();

            if (!orderableNeuron.ordered) {
                deque.putLast(orderableNeuron.neuronOrderId, orderableNeuron.createOrdered());

                for (InputNeuron inputNeuron : orderableNeuron.neuron.getInputs()) {
                    NeuronOrderId inputNeuronOrderId = new NeuronOrderId(inputNeuron.getNeuronId(), orderableNeuron.neuronOrderId.cycle);
                    OrderableNeuron orderableInputNeuron = deque.get(inputNeuronOrderId);

                    if ((orderableInputNeuron == null || !orderableInputNeuron.ordered) && !orderedNeuronIds.contains(inputNeuronOrderId)) {
                        OrderableNeuron orderableInputNeuronFixed = getOrderableOrCreateUnordered(orderableInputNeuron, inputNeuronOrderId);

                        deque.putLast(inputNeuronOrderId, orderableInputNeuronFixed);
                    } else if (orderableInputNeuron != null && orderableInputNeuron.ordered || orderedNeuronIds.contains(inputNeuronOrderId)) {
                        if (inputNeuronOrderId.cycle <= inputNeuron.getRecurrentCyclesAllowed()) {
                            OrderableNeuron orderableInputNeuronFixed = createNextUnordered(inputNeuronOrderId);

                            deque.putLast(orderableInputNeuronFixed.neuronOrderId, orderableInputNeuronFixed);
                        }
                    }
                }
            } else if (orderedNeuronIds.add(orderableNeuron.neuronOrderId)) {
                orderedNeurons.add(orderableNeuron.neuron);
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
    private static final class NeuronOrderId {
        private final SequentialId neuronId;
        private final int cycle;

        private NeuronOrderId createNext() {
            return new NeuronOrderId(neuronId, cycle + 1);
        }

        @Override
        public String toString() {
            return String.format("%s:%d", neuronId, cycle);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class OrderableNeuron {
        private final NeuronOrderId neuronOrderId;
        private final Neuron neuron;
        private final boolean ordered;

        public OrderableNeuron createOrdered() {
            return new OrderableNeuron(neuronOrderId, neuron, true);
        }

        @Override
        public String toString() {
            return String.format("%s:%b", neuron.getId(), ordered);
        }
    }
}
