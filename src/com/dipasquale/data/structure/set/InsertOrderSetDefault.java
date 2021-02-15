package com.dipasquale.data.structure.set;

import com.dipasquale.data.structure.queue.Node;
import com.dipasquale.data.structure.queue.NodeQueue;
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

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
class InsertOrderSetDefault<T> extends AbstractSet<T> implements InsertOrderSet<T> {
    private final Map<T, Node> nodesMap;
    private final NodeQueue<T> nodesQueue;

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
        return nodesQueue.getValue(nodesQueue.first());
    }

    @Override
    public T last() {
        return nodesQueue.getValue(nodesQueue.last());
    }

    @Override
    public T element() {
        Node node = nodesQueue.first();

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

    private Node removeNode() {
        Node node = nodesQueue.first();

        if (node == null) {
            return null;
        }

        nodesMap.remove(nodesQueue.getValue(node));
        nodesQueue.remove(node);

        return node;
    }

    @Override
    public T remove() {
        Node node = removeNode();

        if (node == null) {
            throw new NoSuchElementException();
        }

        return nodesQueue.getValue(node);
    }

    @Override
    public T poll() {
        Node node = removeNode();

        if (node == null) {
            return null;
        }

        return nodesQueue.getValue(node);
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
        Iterable<Node> iterable = nodesQueue::iteratorDescending;

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
