package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialId;
import com.dipasquale.data.structure.map.InsertOrderMap;
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
        InsertOrderMap<SequentialId, Navigation<T>> stack = new InsertOrderMap<>();

        for (NeuronStrategy<T> neuron : outputNeurons) {
            stack.put(neuron.getId(), new Navigation<>(neuron, false));

            while (!stack.isEmpty()) {
                Navigation<T> navigation = stack.removeLast();

                if (!navigation.ready) {
                    stack.put(navigation.neuron.getId(), new Navigation<>(navigation.neuron, true));

                    for (SequentialId id : navigation.neuron.getInputIds()) {
                        Navigation<T> navigationOld = stack.get(id);

                        if (navigationOld != null && !navigationOld.ready) {
                            stack.putLast(id, navigationOld);
                        } else if (navigationOld == null && !orderedAlready.contains(id)) {
                            stack.put(id, new Navigation<>(neurons.get(id), false));
                        } else {
                            navigation.neuron.promoteToRecurrent();
                        }
                    }
                } else {
                    orderedAlready.add(navigation.neuron.getId());
                    ordered.add(navigation.neuron);
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

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Navigation<T extends Neuron> {
        private final NeuronStrategy<T> neuron;
        private final boolean ready;

        @Generated
        @Override
        public String toString() {
            return String.format("%s:%b", neuron.getId(), ready);
        }
    }
}

