package com.dipasquale.data.structure.set;

import com.dipasquale.data.structure.deque.Node;
import com.dipasquale.data.structure.deque.NodeDeque;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class InsertOrderSetDefault<T> extends AbstractSet<T> implements InsertOrderSet<T> {
    private final Map<T, Node> nodesMap;
    private final NodeDeque<T> nodesQueue;

    @Override
    public int size() {
        return nodesMap.size();
    }

    @Override
    public boolean isEmpty() {
        return nodesMap.isEmpty();
    }

    @Override
    public boolean contains(final Object value) {
        return nodesMap.containsKey(value);
    }

    @Override
    public T first() {
        return nodesQueue.getValue(nodesQueue.peekFirst());
    }

    @Override
    public T last() {
        return nodesQueue.getValue(nodesQueue.peekLast());
    }

    @Override
    public T element() {
        Node node = nodesQueue.peekFirst();

        if (node == null) {
            throw new NoSuchElementException();
        }

        return nodesQueue.getValue(node);
    }

    @Override
    public boolean add(final T value) {
        boolean[] added = new boolean[1];

        nodesMap.computeIfAbsent(value, v -> {
            Node node = nodesQueue.createUnbound(v);

            nodesQueue.add(node);
            added[0] = true;

            return node;
        });

        return added[0];
    }

    @Override
    public boolean remove(final Object value) {
        Node node = nodesMap.remove(value);

        if (node == null) {
            return false;
        }

        return nodesQueue.remove(node);
    }

    @Override
    public T remove() {
        Node node = nodesQueue.peekFirst();

        if (node == null) {
            throw new NoSuchElementException();
        }

        T value = nodesQueue.getValue(node);

        remove(value);

        return value;
    }

    @Override
    public T poll() {
        Node node = nodesQueue.peekFirst();

        if (node == null) {
            return null;
        }

        T value = nodesQueue.getValue(node);

        remove(value);

        return value;
    }

    @Override
    public T pop() {
        Node node = nodesQueue.peekLast();

        if (node == null) {
            return null;
        }

        T value = nodesQueue.getValue(node);

        remove(value);

        return value;
    }

    @Override
    public void clear() {
        nodesMap.clear();
        nodesQueue.clear();
    }

    @Override
    public Iterator<T> iterator() {
        return nodesQueue.stream()
                .map(nodesQueue::getValue)
                .iterator();
    }

    @Override
    public Iterator<T> iteratorDescending() {
        Iterable<Node> iterable = nodesQueue::descendingIterator;

        return StreamSupport.stream(iterable.spliterator(), false)
                .map(nodesQueue::getValue)
                .iterator();
    }

    private static Set<?> ensureSet(final Collection<?> collection) {
        if (collection instanceof Set<?>) {
            return (Set<?>) collection;
        }

        return new HashSet<>(collection);
    }

    @Override
    public boolean retainAll(final Collection<?> collection) {
        Set<?> keysToRetain = ensureSet(collection);

        List<T> keysToRemove = StreamSupport.stream(spliterator(), false)
                .filter(k -> !keysToRetain.contains(k))
                .collect(Collectors.toList());

        keysToRemove.forEach(this::remove);

        return !keysToRemove.isEmpty();
    }

    @Override
    public boolean removeAll(final Collection<?> collection) {
        long removed = collection.stream()
                .filter(this::remove)
                .count();

        return removed > 0L;
    }
}
