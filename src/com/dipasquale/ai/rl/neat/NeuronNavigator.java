package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
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
import java.util.Stack;

final class NeuronNavigator<T> implements Iterable<Neuron<T>> {
    private final Map<T, Neuron<T>> neurons = new HashMap<>();
    private final List<Neuron<T>> outputNeurons = new ArrayList<>();
    private Queue<Neuron<T>> neuronsOrdered = null;

    public boolean isEmpty() {
        return neurons.isEmpty();
    }

    public Neuron<T> get(final T id) {
        return neurons.get(id);
    }

    public void add(final Neuron<T> neuron) {
        neurons.put(neuron.getId(), neuron);

        if (neuron.getType() == NodeGeneType.Output) {
            outputNeurons.add(neuron);
        }

        neuronsOrdered = null;
    }

    public void setValue(final NodeGene<T> node, final float value) {
        neurons.get(node.getId()).forceValue(value);
    }

    private Queue<Neuron<T>> createOrdered() { // NOTE: great idea from http://sergebg.blogspot.com/2014/11/non-recursive-dfs-topological-sort.html
        LinkedList<Neuron<T>> ordered = new LinkedList<>();
        Set<T> visited = new HashSet<>();
        Stack<Traversal<T>> stack = new Stack<>();

        for (Neuron<T> neuron : outputNeurons) {
            if (visited.add(neuron.getId())) {
                stack.push(new Traversal<>(neuron, false));

                while (!stack.isEmpty()) {
                    Traversal<T> traversal = stack.pop();

                    if (!traversal.ready) {
                        stack.push(new Traversal<>(traversal.neuron, true));

                        for (T id : traversal.neuron.getInputIds()) {
                            if (visited.add(id)) {
                                stack.push(new Traversal<>(neurons.get(id), false));
                            }
                        }
                    } else {
                        ordered.add(traversal.neuron);
                    }
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

        for (Neuron<T> neuron : outputNeurons) {
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
    public Iterator<Neuron<T>> iterator() {
        ensureOrderedIsInitialized();

        return neuronsOrdered.iterator();
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class Traversal<T> {
        private final Neuron<T> neuron;
        private final boolean ready;
    }
}

/*
    private final Map<DirectedEdge<T>, DirectedEdgePermission<T>> cyclesAllowed = new HashMap<>();

    private boolean isCycleAllowed(final ConnectionGene<T> connection) {
        return !context.connections().allowCyclicConnections()
                || cyclesAllowed.computeIfAbsent(connection.getInnovationId().getDirectedEdge(), de -> new DirectedEdgePermission<>(de, connection.getCyclesAllowed())).isCycleAllowed();
    }
 */
