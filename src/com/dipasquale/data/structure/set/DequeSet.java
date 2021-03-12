package com.dipasquale.data.structure.set;

import com.dipasquale.data.structure.collection.CollectionExtensions;
import com.dipasquale.data.structure.deque.Node;
import com.dipasquale.data.structure.deque.NodeDeque;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntFunction;
import java.util.function.Predicate;

public interface DequeSet<T> extends Set<T> {
    @Override
    default boolean isEmpty() {
        return CollectionExtensions.isEmpty(this);
    }

    T getPrevious(T value);

    T getNext(T value);

    T getFirst();

    T getLast();

    boolean addBefore(T value, T previousToValue);

    boolean addAfter(T value, T nextToValue);

    boolean addFirst(T value);

    boolean addLast(T value);

    @Override
    default boolean add(final T value) {
        return addLast(value);
    }

    T removePrevious(T previousToValue);

    T removeNext(T nextToValue);

    T removeFirst();

    T removeLast();

    Iterator<T> descendingIterator();

    @Override
    default Object[] toArray() {
        return CollectionExtensions.toArray(this);
    }

    @Override
    default <R> R[] toArray(final R[] array) {
        return CollectionExtensions.toArray(this, array);
    }

    @Override
    default <R> R[] toArray(final IntFunction<R[]> generator) {
        return CollectionExtensions.toArray(this, generator);
    }

    @Override
    default boolean containsAll(final Collection<?> collection) {
        return CollectionExtensions.containsAll(this, collection);
    }

    @Override
    default boolean addAll(final Collection<? extends T> collection) {
        return CollectionExtensions.addAll(this, collection);
    }

    @Override
    default boolean removeAll(final Collection<?> collection) {
        return CollectionExtensions.removeAll(this, collection);
    }

    @Override
    default boolean removeIf(final Predicate<? super T> filter) {
        return CollectionExtensions.removeIf(this, filter);
    }

    @Override
    default boolean retainAll(final Collection<?> collection) {
        return CollectionExtensions.retainAll(this, collection);
    }

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
