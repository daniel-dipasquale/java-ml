package com.experimental.ai.rl.neat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

final class NeuronTopologicalNavigator<T> {
    private final Map<T, Neuron<T>> neurons = new HashMap<>();
    private Queue<Neuron<T>> queue = null;

    public Neuron<T> getOrCreate(final NodeGene<T> node, final NeuronFactory<T> neuronFactory) {
        return neurons.computeIfAbsent(node.getId(), nid -> {
            queue = null;

            return neuronFactory.create(node);
        });
    }

    public void setValue(final NodeGene<T> node, final float value) {
        neurons.get(node.getId()).forceValue(value);
    }

    private Queue<Neuron<T>> createQueue() {
        LinkedList<Neuron<T>> neuronsOrdered = new LinkedList<>();
        Set<T> nodeIdsVisited = new HashSet<>();
        Stack<Traversal<T>> neuronStack = new Stack<>();

        for (Neuron<T> neuron : neurons.values()) {
            if (nodeIdsVisited.add(neuron.getNode().getId())) {
                neuronStack.push(new Traversal<>(neuron, false));

                while (!neuronStack.isEmpty()) {
                    Traversal<T> traversal = neuronStack.pop();

                    if (!traversal.ready) {
                        neuronStack.push(new Traversal<>(traversal.neuron, true));

                        for (T nodeId : traversal.neuron.getInputIds()) {
                            if (nodeIdsVisited.add(nodeId)) {
                                neuronStack.push(new Traversal<>(neurons.get(nodeId), false));
                            }
                        }
                    } else {
                        neuronsOrdered.add(traversal.neuron);
                    }
                }
            }
        }

        return neuronsOrdered;
    }

    public Neuron<T> pop() {
        if (queue == null) {
            queue = createQueue();
        }

        return queue.poll();
    }

    @FunctionalInterface
    interface NeuronFactory<T> {
        Neuron<T> create(NodeGene<T> node);
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class Traversal<T> {
        private final Neuron<T> neuron;
        private final boolean ready;
    }

/*
public static List<Integer> topologicalSort(Digraph graph) {
    int n = graph.size();
    boolean[] visited = new boolean[n];
    List<Integer> ordered = new ArrayList<>(n);

    // working horse
    Deque<Integer> stack = new ArrayDeque<Integer>(n);

    for (int s = 0; s < n; s++) {
        if (!visited[s]) {

            // DFS starts here

            visited[s] = true;
            stack.push(s);
            while (!stack.isEmpty()) {
                int v = stack.pop();
                if (v >= 0) {

                    // the trick
                    stack.push(-v - 1);

                    for (int w : graph.adj(v)) {
                        if (!visited[w]) {
                            visited[w] = true;
                            stack.push(w);
                        }
                    }
                } else {

                    // Vertex post-processing happens here
                    ordered.add(-v - 1);

                }
            }


        }
    }
    return ordered;
}
 */
}
