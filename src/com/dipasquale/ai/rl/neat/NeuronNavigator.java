package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialId;
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

final class NeuronNavigator implements Iterable<Neuron> {
    private final Map<SequentialId, Neuron> neurons = new HashMap<>();
    private final List<Neuron> outputNeurons = new ArrayList<>();
    private Queue<Neuron> neuronsOrdered = null;

    public boolean isEmpty() {
        return neurons.isEmpty();
    }

    public Neuron get(final SequentialId id) {
        return neurons.get(id);
    }

    public void add(final Neuron neuron) {
        neurons.put(neuron.getId(), neuron);

        if (neuron.getType() == NodeGeneType.Output) {
            outputNeurons.add(neuron);
        }

        neuronsOrdered = null;
    }

    private Queue<Neuron> createOrdered() { // NOTE: great idea from http://sergebg.blogspot.com/2014/11/non-recursive-dfs-topological-sort.html
        LinkedList<Neuron> ordered = new LinkedList<>();
        Set<SequentialId> visited = new HashSet<>();
        Stack<Traversal> stack = new Stack<>();

        for (Neuron neuron : outputNeurons) {
            if (visited.add(neuron.getId())) {
                stack.push(new Traversal(neuron, false));

                while (!stack.isEmpty()) {
                    Traversal traversal = stack.pop();

                    if (!traversal.ready) {
                        stack.push(new Traversal(traversal.neuron, true));

                        for (SequentialId id : traversal.neuron.getInputIds()) {
                            if (visited.add(id)) {
                                stack.push(new Traversal(neurons.get(id), false));
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
    private static final class Traversal {
        private final Neuron neuron;
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
