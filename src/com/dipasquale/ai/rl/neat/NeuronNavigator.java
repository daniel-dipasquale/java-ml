package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.data.structure.map.HashDequeMap;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NeuronNavigator<T extends Neuron> implements Iterable<Neuron> {
    private final NeuronPromoter<T> neuronPromoter;
    private final Map<SequentialId, NeuronStrategy<T>> neurons = new HashMap<>();
    private final List<NeuronStrategy<T>> outputNeurons = new ArrayList<>();
    private Collection<Neuron> neuronsOrdered = null;

    public boolean isEmpty() {
        return neurons.isEmpty();
    }

    public Neuron get(final SequentialId id) {
        return neurons.get(id);
    }

    public void add(final Neuron neuron) {
        NeuronStrategy<T> neuronStrategy = new NeuronStrategy<>(neuronPromoter, neuron);

        neurons.put(neuron.getId(), neuronStrategy);

        if (neuron.getType() == NodeGeneType.Output) {
            outputNeurons.add(neuronStrategy);
        }

        neuronsOrdered = null;
    }

    private void addAllPathsFrom(final NeuronStrategy<T> recurrentNeuron, final Iterable<NeuronStrategy<T>> sourceNeurons, final HashDequeMap<NavigationId, PitStop<T>> neuronStack, final int cycle) {
        for (NeuronStrategy<T> neuron : sourceNeurons) {
            neuronStack.put(new NavigationId(neuron.getId(), cycle), PitStop.createNotReady(neuron));
        }
    }

    private Collection<Neuron> createOrdered() {
//        DequeMap<SequentialId, Neuron> ordered = new DequeMap<>();
//        DequeMap<NavigationId, PitStop<T>> stack = new DequeMap<>();
//
//        for (NeuronStrategy<T> neuron : outputNeurons) {
//            stack.put(new NavigationId(neuron.getId(), 0), PitStop.createNotReady(neuron));
//
//            while (!stack.isEmpty()) {
//                PitStop<T> pitStop = stack.removeLast();
//
//                if (!pitStop.ready) {
//                    stack.put(new NavigationId(pitStop.neuron.getId(), 0), PitStop.createReady(pitStop.neuron));
//
//                    for (SequentialId id : pitStop.neuron.getInputIds()) {
//                        PitStop<T> pitStopOld = stack.get(id);
//
//                        if (pitStopOld != null && !pitStopOld.ready) {
//                            stack.putLast(new NavigationId(id, 0), pitStopOld);
//                        } else if (pitStopOld == null && !ordered.containsKey(id)) {
//                            stack.put(new NavigationId(id, 0), PitStop.createNotReady(neurons.get(id)));
//                        } else {
//                            pitStop.neuron.promoteToRecurrent();
//                        }
//                    }
//                } else {
//                    orderedAlready.add(pitStop.neuron.getId());
//                    ordered.add(pitStop.neuron);
//                }
//            }
//        }
//
//        return ordered;
        return null;
    }

    private void ensureOrderedIsInitialized() {
        if (neuronsOrdered == null) {
            neuronsOrdered = createOrdered();
        }
    }

    public float[] getOutputValues() {
        float[] outputValues = new float[outputNeurons.size()];
        int index = 0;

        for (Neuron neuron : outputNeurons) {
            outputValues[index++] = neuron.getValue();
        }

        return outputValues;
    }

    public void clear() {
        neurons.clear();
        outputNeurons.clear();
        neuronsOrdered = null;
    }

    @Override
    public Iterator<Neuron> iterator() {
        ensureOrderedIsInitialized();

        return neuronsOrdered.iterator();
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    @EqualsAndHashCode
    private static final class NavigationId {
        private final SequentialId id;
        private final int cycle;

        @Generated
        @Override
        public String toString() {
            return String.format("%s:%d", id, cycle);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class PitStop<T extends Neuron> {
        private final NeuronStrategy<T> neuron;
        private final boolean ready;

        private static <T extends Neuron> PitStop<T> createNotReady(final NeuronStrategy<T> neuron) {
            return new PitStop<>(neuron, false);
        }

        private static <T extends Neuron> PitStop<T> createReady(final NeuronStrategy<T> neuron) {
            return new PitStop<>(neuron, true);
        }

        @Generated
        @Override
        public String toString() {
            return String.format("%s:%b", neuron.getId(), ready);
        }
    }
}

/*
package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.data.structure.map.DequeMap;
import lombok.AccessLevel;
import lombok.Generated;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NeuronNavigator<T extends Neuron> implements Iterable<Neuron> {
    private final NeuronPromoter<T> neuronPromoter;
    private final Map<SequentialId, NeuronStrategy<T>> neurons = new HashMap<>();
    private final List<NeuronStrategy<T>> outputNeurons = new ArrayList<>();
    private Queue<Neuron> neuronsOrdered = null;

    public boolean isEmpty() {
        return neurons.isEmpty();
    }

    public Neuron get(final SequentialId id) {
        return neurons.get(id);
    }

    public void add(final Neuron neuron) {
        NeuronStrategy<T> neuronStrategy = new NeuronStrategy<>(neuronPromoter, neuron);

        neurons.put(neuron.getId(), neuronStrategy);

        if (neuron.getType() == NodeGeneType.Output) {
            outputNeurons.add(neuronStrategy);
        }

        neuronsOrdered = null;
    }

    private Queue<Neuron> createOrdered() {
        LinkedList<Neuron> ordered = new LinkedList<>();
        Set<SequentialId> orderedAlready = new HashSet<>();
        DequeMap<SequentialId, PitStop<T>> stack = new DequeMap<>();

        for (NeuronStrategy<T> neuron : outputNeurons) {
            stack.put(neuron.getId(), new PitStop<>(neuron, false));

            while (!stack.isEmpty()) {
                PitStop<T> pitStop = stack.removeLast();

                if (!pitStop.ready) {
                    stack.put(pitStop.neuron.getId(), new PitStop<>(pitStop.neuron, true));

                    for (SequentialId id : pitStop.neuron.getInputIds()) {
                        PitStop<T> pitStopOld = stack.get(id);

                        if (pitStopOld != null && !pitStopOld.ready) {
                            stack.putLast(id, pitStopOld);
                        } else if (pitStopOld == null && !orderedAlready.contains(id)) {
                            stack.put(id, new PitStop<>(neurons.get(id), false));
                        } else {
                            pitStop.neuron.promoteToRecurrent();
                        }
                    }
                } else {
                    orderedAlready.add(pitStop.neuron.getId());
                    ordered.add(pitStop.neuron);
                }
            }
        }

        return ordered;
    }

    private void ensureOrderedIsInitialized() {
        if (neuronsOrdered == null) {
            neuronsOrdered = createOrdered();
        }
    }

    public float[] getOutputValues() {
        float[] outputValues = new float[outputNeurons.size()];
        int index = 0;

        for (Neuron neuron : outputNeurons) {
            outputValues[index++] = neuron.getValue();
        }

        return outputValues;
    }

    public void clear() {
        neurons.clear();
        outputNeurons.clear();
        neuronsOrdered = null;
    }

    @Override
    public Iterator<Neuron> iterator() {
        ensureOrderedIsInitialized();

        return neuronsOrdered.iterator();
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class NavigationId {
        private final SequentialId id;
        private final int cycle;

        @Generated
        @Override
        public String toString() {
            return String.format("%s:%d", id, cycle);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class PitStop<T extends Neuron> {
        private final NeuronStrategy<T> neuron;
        private final boolean ready;

        @Generated
        @Override
        public String toString() {
            return String.format("%s:%b", neuron.getId(), ready);
        }
    }
}
 */