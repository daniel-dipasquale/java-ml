package com.dipasquale.data.structure.deque;

import com.dipasquale.common.ArgumentValidator;
import com.dipasquale.data.structure.collection.CollectionExtensions;
import com.dipasquale.data.structure.iterator.LinkedIterator;

import java.util.Iterator;

final class NodeDequeDefault<T> implements NodeDeque<T> {
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

    private boolean hasMembership(final Node node) {
        return node != null && node.getMembership() == membership;
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

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
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

    @Override
    public Node peekFirst() {
        return peekNext(start);
    }

    @Override
    public Node peekLast() {
        return peekPrevious(end);
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

    private void offerBefore(final NodeDefault<T> node, final NodeDefault<T> previousToNode) {
        if (canBeRemoved(node)) {
            remove(node);
        }

        node.previous = previousToNode.previous;
        node.next = previousToNode;
        previousToNode.previous.next = node;
        previousToNode.previous = node;
        size++;
    }

    @Override
    public boolean offerBefore(final Node node, final Node previousToNode) {
        if (!hasMembership(node) || !hasMembership(previousToNode)) {
            return false;
        }

        offerBefore((NodeDefault<T>) node, (NodeDefault<T>) previousToNode);

        return true;
    }

    private void offerAfter(final NodeDefault<T> node, final NodeDefault<T> nextToNode) {
        if (canBeRemoved(node)) {
            remove(node);
        }

        node.previous = nextToNode;
        node.next = nextToNode.next;
        nextToNode.next.previous = node;
        nextToNode.next = node;
        size++;
    }

    @Override
    public boolean offerAfter(final Node node, final Node nextToNode) {
        if (!hasMembership(node) || !hasMembership(nextToNode)) {
            return false;
        }

        offerAfter((NodeDefault<T>) node, (NodeDefault<T>) nextToNode);

        return true;
    }

    @Override
    public boolean offerFirst(final Node node) {
        if (!hasMembership(node)) {
            return false;
        }

        offerAfter((NodeDefault<T>) node, start);

        return true;
    }

    @Override
    public boolean offerLast(final Node node) {
        if (!hasMembership(node)) {
            return false;
        }

        offerBefore((NodeDefault<T>) node, end);

        return true;
    }

    private void ensureHasMembership(final Node node) {
        ArgumentValidator.ensureTrue(hasMembership(node), "node", "was not created by this deque");
    }

    @Override
    public void addBefore(final Node node, final Node previousToNode) {
        ensureHasMembership(node);
        ensureHasMembership(previousToNode);
        offerBefore(node, previousToNode);
    }

    @Override
    public void addAfter(final Node node, final Node nextToNode) {
        ensureHasMembership(node);
        ensureHasMembership(nextToNode);
        offerAfter(node, nextToNode);
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

    @Override
    public boolean equals(final Object other) {
        return CollectionExtensions.equals(this, other);
    }

    @Override
    public int hashCode() {
        return CollectionExtensions.hashCode(this);
    }

    @Override
    public String toString() {
        return CollectionExtensions.toString(this);
    }
}
