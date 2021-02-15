package com.dipasquale.data.structure.queue;

import com.dipasquale.common.ArgumentValidator;
import com.dipasquale.data.structure.iterator.LinkedIterator;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

final class NodeQueueDefault<T> extends AbstractQueue<Node> implements NodeQueue<T> {
    protected Object membership;
    protected NodeDefault<T> start;
    protected NodeDefault<T> end;
    private int size;

    NodeQueueDefault() {
        initialize();
    }

    private void initialize() {
        Object membership = new Object();
        NodeDefault<T> start = new NodeDefault<>(null, membership);
        NodeDefault<T> end = new NodeDefault<>(null, membership);

        start.next = end;
        end.previous = start;
        this.membership = membership;
        this.start = start;
        this.end = end;
        this.size = 0;
    }

    private NodeDefault<T> createUnlinkedTyped(final T value) {
        return new NodeDefault<>(value, membership);
    }

    @Override
    public Node createUnbound(final T value) {
        return createUnlinkedTyped(value);
    }

    @Override
    public int size() {
        return size;
    }

    private boolean hasMembership(final Node node) {
        return node != null && node.getMembership() == membership;
    }

    private boolean canBeAdded(final NodeDefault<T> node) {
        return node.previous == null;
    }

    @Override
    public boolean contains(final Object object) {
        if (!(object instanceof Node)) {
            return false;
        }

        return hasMembership((Node) object) && !canBeAdded((NodeDefault<T>) object);
    }

    @Override
    public final Node first() {
        if (start.next == end) {
            return null;
        }

        return start.next;
    }

    @Override
    public Node last() {
        if (end.previous == start) {
            return null;
        }

        return end.previous;
    }

    private NodeDefault<T> previous(final NodeDefault<T> node) {
        if (node.previous == start) {
            return null;
        }

        return node.previous;
    }

    @Override
    public Node previous(final Node node) {
        if (!hasMembership(node)) {
            return null;
        }

        return previous((NodeDefault<T>) node);
    }

    private NodeDefault<T> next(final NodeDefault<T> node) {
        if (node.next == end) {
            return null;
        }

        return node.next;
    }

    @Override
    public Node next(final Node node) {
        if (!hasMembership(node)) {
            return null;
        }

        return next((NodeDefault<T>) node);
    }

    private T getValue(final NodeDefault<T> node) {
        return node.value;
    }

    public T getValue(final Node node) {
        if (!hasMembership(node)) {
            return null;
        }

        return getValue((NodeDefault<T>) node);
    }

    private void ensureHasMembership(final Node node) {
        ArgumentValidator.getInstance().ensureTrue(hasMembership(node), "node", "was not created by this queue");
    }

    private boolean add(final NodeDefault<T> node) {
        if (!canBeAdded(node)) {
            return false;
        }

        node.previous = end.previous;
        node.next = end;
        end.previous.next = node;
        end.previous = node;
        size++;

        return true;
    }

    @Override
    public boolean add(final Node node) {
        ensureHasMembership(node);

        if (add((NodeDefault<T>) node)) {
            return true;
        }

        throw new IllegalArgumentException("node was already added");
    }

    @Override
    public boolean offer(final Node node) {
        return hasMembership(node) && add((NodeDefault<T>) node);
    }

    private boolean canBeRemoved(final NodeDefault<T> node) {
        return node.previous != null;
    }

    private NodeDefault<T> remove(final NodeDefault<T> node) {
        node.next.previous = node.previous;
        node.previous.next = node.next;
        node.previous = null;
        node.next = null;
        size--;

        return node;
    }

    private boolean remove(final Node node) {
        if (!hasMembership(node)) {
            return false;
        }

        NodeDefault<T> nodeFixed = (NodeDefault<T>) node;

        if (!canBeRemoved(nodeFixed)) {
            return false;
        }

        remove(nodeFixed);

        return true;
    }

    @Override
    public final boolean remove(final Object object) {
        if (!(object instanceof Node)) {
            return false;
        }

        return remove((Node) object);
    }

    private boolean reoffer(final NodeDefault<T> node) {
        if (!canBeRemoved(node)) {
            return false;
        }

        remove(node);

        return add(node);
    }

    @Override
    public boolean reoffer(final Node node) {
        if (!hasMembership(node)) {
            return false;
        }

        return reoffer((NodeDefault<T>) node);
    }

    @Override
    public Node poll() {
        if (start.next == end) {
            return null;
        }

        return remove(start.next);
    }

    @Override
    public void clear() {
        initialize();
    }

    @Override
    public Iterator<Node> iterator() {
        return LinkedIterator.createStream(start.next, e -> e.next, e -> e != end)
                .map(e -> (Node) e)
                .iterator();
    }

    @Override
    public Iterator<Node> iteratorDescending() {
        return LinkedIterator.createStream(end.previous, e -> e.previous, e -> e != start)
                .map(e -> (Node) e)
                .iterator();
    }

    private static Set<?> ensureSet(final Collection<?> collection) {
        Set<Object> set = Collections.newSetFromMap(new IdentityHashMap<>());

        set.addAll(collection);

        return set;
    }

    @Override
    public boolean retainAll(final Collection<?> collection) {
        Set<?> nodesToRetain = ensureSet(collection);

        List<Node> nodesToRemove = StreamSupport.stream(spliterator(), false)
                .filter(k -> !nodesToRetain.contains(k))
                .collect(Collectors.toList());

        nodesToRemove.forEach(this::remove);

        return !nodesToRemove.isEmpty();
    }

    @Override
    public boolean removeAll(final Collection<?> collection) {
        long removed = collection.stream()
                .filter(this::remove)
                .count();

        return removed > 0L;
    }
}
