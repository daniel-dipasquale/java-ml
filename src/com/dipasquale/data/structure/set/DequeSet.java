package com.dipasquale.data.structure.set;

import com.dipasquale.data.structure.deque.Node;
import com.dipasquale.data.structure.deque.NodeDeque;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public interface DequeSet<T> extends Set<T> {
    T getPrevious(T value);

    T getNext(T value);

    T getFirst();

    T getLast();

    boolean addBefore(T value, T previousToValue);

    boolean addAfter(T value, T nextToValue);

    boolean addFirst(T value);

    boolean addLast(T value);

    T removePrevious(T previousToValue);

    T removeNext(T nextToValue);

    T removeFirst();

    T removeLast();

    Iterator<T> descendingIterator();

    static <T> DequeSet<T> create() {
        Map<T, Node> nodesMap = new HashMap<>();
        NodeDeque<T> nodesQueue = NodeDeque.create();

        return new DequeSetDefault<>(nodesMap, nodesQueue);
    }

    static <T> DequeSet<T> create(final int initialCapacity) {
        Map<T, Node> nodesMap = new HashMap<>(initialCapacity);
        NodeDeque<T> nodesQueue = NodeDeque.create();

        return new DequeSetDefault<>(nodesMap, nodesQueue);
    }

    static <T> DequeSet<T> createSynchronized(final int initialCapacity) {
        DequeSet<T> set = create(initialCapacity);

        return new DequeSetSynchronized<>(set);
    }

    static <T> DequeSet<T> createSynchronized() {
        DequeSet<T> set = create();

        return new DequeSetSynchronized<>(set);
    }

    static <T> DequeSet<T> createConcurrent() {
        Map<T, Node> nodesMap = new ConcurrentHashMap<>();
        NodeDeque<T> nodesQueue = NodeDeque.createSynchronized();

        return new DequeSetDefault<>(nodesMap, nodesQueue);
    }

    static <T> DequeSet<T> createConcurrent(final int initialCapacity, final float loadFactor, final int numberOfThreads) {
        Map<T, Node> nodesMap = new ConcurrentHashMap<>(initialCapacity, loadFactor, numberOfThreads);
        NodeDeque<T> nodesQueue = NodeDeque.createSynchronized();

        return new DequeSetDefault<>(nodesMap, nodesQueue);
    }
}
