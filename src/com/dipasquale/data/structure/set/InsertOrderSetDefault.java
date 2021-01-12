package com.dipasquale.data.structure.set;

import com.dipasquale.data.structure.queue.Node;
import com.dipasquale.data.structure.queue.NodeQueue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class InsertOrderSetDefault<T> extends AbstractSet<T> implements InsertOrderSet<T>, Queue<T> {
    private final Map<T, Node> nodesMap;
    private final NodeQueue<T> nodesQueue;

    public InsertOrderSetDefault() {
        this(new HashMap<>(), NodeQueue.create());
    }

    private static Set<?> ensureSet(final Collection<?> collection) {
        if (collection instanceof Set<?>) {
            return (Set<?>) collection;
        }

        return new HashSet<>(collection);
    }

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
    public boolean add(final T value) {
        boolean[] added = new boolean[]{false};

        nodesMap.computeIfAbsent(value, v -> {
            Node node = nodesQueue.createUnlinked(v);

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
    public boolean offer(final T value) {
        return add(value);
    }

    private Node removeNode() {
        Node node = nodesQueue.first();

        if (node == null) {
            return null;
        }

        node = nodesMap.remove(nodesQueue.getValue(node));

        if (node == null) {
            return null;
        }

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
    public T element() {
        T value = first();

        if (value == null) {
            throw new NoSuchElementException();
        }

        return value;
    }

    @Override
    public T peek() {
        return first();
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
}
