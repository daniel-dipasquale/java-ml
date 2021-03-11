package com.dipasquale.data.structure.set;

import com.dipasquale.data.structure.deque.Node;
import com.dipasquale.data.structure.deque.NodeDeque;

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

    T pop();

    Iterator<T> iteratorDescending();

    static <T> InsertOrderSet<T> create() {
        Map<T, Node> nodesMap = new HashMap<>();
        NodeDeque<T> nodesQueue = NodeDeque.create();

        return new InsertOrderSetDefault<>(nodesMap, nodesQueue);
    }

    static <T> InsertOrderSet<T> create(final int initialCapacity) {
        Map<T, Node> nodesMap = new HashMap<>(initialCapacity);
        NodeDeque<T> nodesQueue = NodeDeque.create();

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
        NodeDeque<T> nodesQueue = NodeDeque.createSynchronized();

        return new InsertOrderSetDefault<>(nodesMap, nodesQueue);
    }

    static <T> InsertOrderSet<T> createConcurrent(final int initialCapacity, final float loadFactor, final int numberOfThreads) {
        Map<T, Node> nodesMap = new ConcurrentHashMap<>(initialCapacity, loadFactor, numberOfThreads);
        NodeDeque<T> nodesQueue = NodeDeque.createSynchronized();

        return new InsertOrderSetDefault<>(nodesMap, nodesQueue);
    }
}
