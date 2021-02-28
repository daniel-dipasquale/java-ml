package com.dipasquale.data.structure.set;

import com.dipasquale.data.structure.queue.Node;
import com.dipasquale.data.structure.queue.NodeQueue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public interface InsertOrderSet<T> extends Set<T>, Queue<T> {
    T first();

    T last();

    @Override
    default T peek() {
        return first();
    }

    @Override
    default boolean offer(final T value) {
        return add(value);
    }

    Iterator<T> iteratorDescending();

    static <T> InsertOrderSet<T> create() {
        Map<T, Node> nodesMap = new HashMap<>();
        NodeQueue<T> nodesQueue = NodeQueue.create();

        return new InsertOrderSetDefault<>(nodesMap, nodesQueue);
    }

    static <T> InsertOrderSet<T> create(final int initialCapacity) {
        Map<T, Node> nodesMap = new HashMap<>(initialCapacity);
        NodeQueue<T> nodesQueue = NodeQueue.create();

        return new InsertOrderSetDefault<>(nodesMap, nodesQueue);
    }

    static <T> InsertOrderSet<T> createSynchronized(final int initialCapacity) {
        InsertOrderSet<T> set = create(initialCapacity);

        return new InsertOrderSetSynchronized<>(set);
    }

    static <T> InsertOrderSet<T> createSynchronized() {
        InsertOrderSet<T> set = create();

        return new InsertOrderSetSynchronized<>(set);
    }

    static <T> InsertOrderSet<T> createConcurrent() {
        Map<T, Node> nodesMap = new ConcurrentHashMap<>();
        NodeQueue<T> nodesQueue = NodeQueue.createSynchronized();

        return new InsertOrderSetDefault<>(nodesMap, nodesQueue);
    }

    static <T> InsertOrderSet<T> createConcurrent(final int initialCapacity, final float loadFactor, final int numberOfThreads) {
        Map<T, Node> nodesMap = new ConcurrentHashMap<>(initialCapacity, loadFactor, numberOfThreads);
        NodeQueue<T> nodesQueue = NodeQueue.createSynchronized();

        return new InsertOrderSetDefault<>(nodesMap, nodesQueue);
    }
}
