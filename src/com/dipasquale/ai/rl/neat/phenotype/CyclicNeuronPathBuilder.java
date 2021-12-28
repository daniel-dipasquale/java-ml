package com.dipasquale.ai.rl.neat.phenotype;

import com.dipasquale.ai.rl.neat.internal.Id;
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
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class CyclicNeuronPathBuilder implements NeuronPathBuilder, Serializable {
    @Serial
    private static final long serialVersionUID = 1299761107234360133L;
    private final Map<Id, Neuron> neurons = new HashMap<>();
    private final Set<NeuronOrderId> orderedNeuronIds = new HashSet<>();
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
        HashDequeMap<NeuronOrderId, OrderableNeuron> orderingNeurons = new HashDequeMap<>();

        addRootTo(orderingNeurons, neuron);

        while (!orderingNeurons.isEmpty()) {
            OrderableNeuron orderableNeuron = orderingNeurons.removeLast();

            if (!orderableNeuron.ordered) {
                orderingNeurons.putLast(orderableNeuron.neuronOrderId, orderableNeuron.createOrdered());

                for (NeuronInputConnection connection : orderableNeuron.neuron.getInputConnections()) {
                    NeuronOrderId sourceNeuronId = new NeuronOrderId(connection.getSourceNeuronId(), orderableNeuron.neuronOrderId.cycle);

                    if (sourceNeuronId.cycle < connection.getCyclesAllowed()) {
                        OrderableNeuron orderableSourceNeuron = orderingNeurons.get(sourceNeuronId);

                        if ((orderableSourceNeuron == null || !orderableSourceNeuron.ordered) && !orderedNeuronIds.contains(sourceNeuronId)) {
                            OrderableNeuron orderableSourceNeuronFixed = getOrderableOrCreateUnordered(orderableSourceNeuron, sourceNeuronId);

                            orderingNeurons.putLast(sourceNeuronId, orderableSourceNeuronFixed);
                        } else if (orderableSourceNeuron != null && orderableSourceNeuron.ordered || orderedNeuronIds.contains(sourceNeuronId)) {
                            OrderableNeuron orderableSourceNeuronFixed = createNextUnordered(sourceNeuronId);

                            orderingNeurons.putLast(orderableSourceNeuronFixed.neuronOrderId, orderableSourceNeuronFixed);
                        }
                    }
                }
            } else if (orderedNeuronIds.add(orderableNeuron.neuronOrderId) && !orderingNeurons.isEmpty()) {
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
    private static final class NeuronOrderId implements Serializable {
        @Serial
        private static final long serialVersionUID = -445055006068211779L;
        private final Id neuronId;
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
    private static final class OrderableNeuron implements Serializable {
        @Serial
        private static final long serialVersionUID = -4219700326308862911L;
        private final NeuronOrderId neuronOrderId;
        private final Neuron neuron;
        private final boolean ordered;

        public OrderableNeuron createOrdered() {
            return new OrderableNeuron(neuronOrderId, neuron, true);
        }

        @Override
        public String toString() {
            return String.format("%s:%b", neuronOrderId, ordered);
        }
    }
}
