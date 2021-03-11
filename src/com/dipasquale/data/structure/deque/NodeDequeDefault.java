package com.dipasquale.data.structure.deque;

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

final class NodeDequeDefault<T> extends AbstractQueue<Node> implements NodeDeque<T> {
    private Object membership;
    private NodeDefault<T> start;
    private NodeDefault<T> end;
    private int size;

    NodeDequeDefault() {
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
        if (object instanceof Node) {
            return hasMembership((Node) object) && !canBeAdded((NodeDefault<T>) object);
        }

        return false;
    }

    @Override
    public Node peekFirst() {
        if (start.next == end) {
            return null;
        }

        return start.next;
    }

    @Override
    public Node peekLast() {
        if (end.previous == start) {
            return null;
        }

        return end.previous;
    }

    private NodeDefault<T> peekPrevious(final NodeDefault<T> node) {
        if (node.previous == start) {
            return null;
        }

        return node.previous;
    }

    @Override
    public Node peekPrevious(final Node node) {
        if (!hasMembership(node)) {
            return null;
        }

        return peekPrevious((NodeDefault<T>) node);
    }

    private NodeDefault<T> peekNext(final NodeDefault<T> node) {
        if (node.next == end) {
            return null;
        }

        return node.next;
    }

    @Override
    public Node peekNext(final Node node) {
        if (!hasMembership(node)) {
            return null;
        }

        return peekNext((NodeDefault<T>) node);
    }

    private T getValue(final NodeDefault<T> node) {
        return node.value;
    }

    @Override
    public T getValue(final Node node) {
        if (!hasMembership(node)) {
            return null;
        }

        return getValue((NodeDefault<T>) node);
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

    private void offerFirst(final NodeDefault<T> node) {
        if (canBeRemoved(node)) {
            remove(node);
        }

        node.previous = start;
        node.next = start.next;
        start.next.previous = node;
        start.next = node;
        size++;
    }

    @Override
    public boolean offerFirst(final Node node) {
        if (!hasMembership(node)) {
            return false;
        }

        offerFirst((NodeDefault<T>) node);

        return true;
    }

    private void offerLast(final NodeDefault<T> node) {
        if (canBeRemoved(node)) {
            remove(node);
        }

        node.previous = end.previous;
        node.next = end;
        end.previous.next = node;
        end.previous = node;
        size++;
    }

    @Override
    public boolean offerLast(final Node node) {
        if (!hasMembership(node)) {
            return false;
        }

        offerLast((NodeDefault<T>) node);

        return true;
    }

    private void ensureHasMembership(final Node node) {
        ArgumentValidator.getInstance().ensureTrue(hasMembership(node), "node", "was not created by this queue");
    }

    @Override
    public void addFirst(final Node node) {
        ensureHasMembership(node);
        offerFirst(node);
    }

    @Override
    public void addLast(final Node node) {
        ensureHasMembership(node);
        offerLast(node);
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
    public final boolean remove(final Object node) {
        if (node instanceof Node) {
            return remove((Node) node);
        }

        return false;
    }

    @Override
    public Node removeFirst() {
        if (start.next == end) {
            return null;
        }

        return remove(start.next);
    }

    @Override
    public Node removeLast() {
        if (end.previous == start) {
            return null;
        }

        return remove(end.previous);
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
    public Iterator<Node> descendingIterator() {
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
