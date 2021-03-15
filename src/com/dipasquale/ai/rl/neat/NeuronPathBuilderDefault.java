package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialId;
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

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NeuronPathBuilderDefault implements NeuronPathBuilder {
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
    public void addPathLeadingTo(final Neuron neuron) {
        HashDequeMap<SequentialId, Pitstop> deque = new HashDequeMap<>();

        deque.putLast(neuron.getId(), new Pitstop(neuron, false));

        while (!deque.isEmpty()) {
            Pitstop pitstop = deque.removeLast();

            if (!pitstop.ready) {
                deque.putLast(pitstop.neuron.getId(), new Pitstop(pitstop.neuron, true));

                for (SequentialId id : pitstop.neuron.getInputIds()) {
                    Pitstop pitstopOld = deque.get(id);

                    if ((pitstopOld == null || !pitstopOld.ready) && !alreadyOrdered.contains(id)) {
                        if (pitstopOld == null) {
                            deque.putLast(id, new Pitstop(neurons.get(id), false));
                        } else {
                            deque.putLast(id, pitstopOld);
                        }
                    }
                }
            } else if (alreadyOrdered.add(pitstop.neuron.getId())) {
                ordered.add(pitstop.neuron);
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
    private static final class Pitstop {
        private final Neuron neuron;
        private final boolean ready;

        @Override
        public String toString() {
            return String.format("%s:%b", neuron.getId(), ready);
        }
    }
}
