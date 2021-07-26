package com.dipasquale.data.structure.deque;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.data.structure.iterator.LinkedIterator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Iterator;

public final class SimpleNodeDeque<T> extends AbstractDeque<SimpleNode<T>> implements NodeDeque<T, SimpleNode<T>>, Serializable {
    @Serial
    private static final long serialVersionUID = -6511910907907223574L;
    private Object membership;
    private SimpleNode<T> start;
    private SimpleNode<T> end;
    private int size;

    public SimpleNodeDeque() {
        initialize();
    }

    private void initialize() {
        Object membership = new Membership();
        SimpleNode<T> start = new SimpleNode<>(null, membership);
        SimpleNode<T> end = new SimpleNode<>(null, membership);

        start.next = end;
        end.previous = start;
        this.membership = membership;
        this.start = start;
        this.end = end;
        this.size = 0;
    }

    @Override
    public SimpleNode<T> createUnbound(final T value) {
        return new SimpleNode<>(value, membership);
    }

    private boolean hasMembership(final SimpleNode<T> node) {
        return node != null && node.membership == membership;
    }

    private T getValueInternal(final SimpleNode<T> node) {
        return node.value;
    }

    @Override
    public T getValue(final SimpleNode<T> node) {
        if (!hasMembership(node)) {
            return null;
        }

        return getValueInternal(node);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private boolean canBeAdded(final SimpleNode<T> node) {
        return node.previous == null;
    }

    @Override
    public boolean contains(final Object node) {
        if (node instanceof SimpleNode<?>) {
            SimpleNode<T> nodeFixed = (SimpleNode<T>) node;

            return hasMembership(nodeFixed) && !canBeAdded(nodeFixed);
        }

        return false;
    }

    private SimpleNode<T> peekPreviousInternal(final SimpleNode<T> node) {
        if (node.previous == start) {
            return null;
        }

        return node.previous;
    }

    @Override
    public SimpleNode<T> peekPrevious(final SimpleNode<T> node) {
        if (!hasMembership(node)) {
            return null;
        }

        return peekPreviousInternal(node);
    }

    private SimpleNode<T> peekNextInternal(final SimpleNode<T> node) {
        if (node.next == end) {
            return null;
        }

        return node.next;
    }

    @Override
    public SimpleNode<T> peekNext(final SimpleNode<T> node) {
        if (!hasMembership(node)) {
            return null;
        }

        return peekNextInternal(node);
    }

    @Override
    public SimpleNode<T> peekFirst() {
        return peekNextInternal(start);
    }

    @Override
    public SimpleNode<T> peekLast() {
        return peekPreviousInternal(end);
    }

    private boolean canBeRemoved(final SimpleNode<T> node) {
        return node.previous != null;
    }

    private SimpleNode<T> removeInternal(final SimpleNode<T> node) {
        node.next.previous = node.previous;
        node.previous.next = node.next;
        node.previous = null;
        node.next = null;
        size--;

        return node;
    }

    private void offerBeforeInternal(final SimpleNode<T> node, final SimpleNode<T> previousToNode) {
        if (canBeRemoved(node)) {
            removeInternal(node);
        }

        node.previous = previousToNode.previous;
        node.next = previousToNode;
        previousToNode.previous.next = node;
        previousToNode.previous = node;
        size++;
    }

    @Override
    public boolean offerBefore(final SimpleNode<T> node, final SimpleNode<T> previousToNode) {
        if (!hasMembership(node) || !hasMembership(previousToNode)) {
            return false;
        }

        offerBeforeInternal(node, previousToNode);

        return true;
    }

    private void offerAfterInternal(final SimpleNode<T> node, final SimpleNode<T> nextToNode) {
        if (canBeRemoved(node)) {
            removeInternal(node);
        }

        node.previous = nextToNode;
        node.next = nextToNode.next;
        nextToNode.next.previous = node;
        nextToNode.next = node;
        size++;
    }

    @Override
    public boolean offerAfter(final SimpleNode<T> node, final SimpleNode<T> nextToNode) {
        if (!hasMembership(node) || !hasMembership(nextToNode)) {
            return false;
        }

        offerAfterInternal(node, nextToNode);

        return true;
    }

    @Override
    public boolean offerFirst(final SimpleNode<T> node) {
        if (!hasMembership(node)) {
            return false;
        }

        offerAfterInternal(node, start);

        return true;
    }

    @Override
    public boolean offerLast(final SimpleNode<T> node) {
        if (!hasMembership(node)) {
            return false;
        }

        offerBeforeInternal(node, end);

        return true;
    }

    private void ensureHasMembership(final SimpleNode<T> node) {
        ArgumentValidatorSupport.ensureTrue(hasMembership(node), "node", "was not created by this deque");
    }

    @Override
    public void addBefore(final SimpleNode<T> node, final SimpleNode<T> previousToNode) {
        ensureHasMembership(node);
        ensureHasMembership(previousToNode);
        offerBefore(node, previousToNode);
    }

    @Override
    public void addAfter(final SimpleNode<T> node, final SimpleNode<T> nextToNode) {
        ensureHasMembership(node);
        ensureHasMembership(nextToNode);
        offerAfter(node, nextToNode);
    }

    @Override
    public void addFirst(final SimpleNode<T> node) {
        ensureHasMembership(node);
        offerFirst(node);
    }

    @Override
    public void addLast(final SimpleNode<T> node) {
        ensureHasMembership(node);
        offerLast(node);
    }

    @Override
    public final boolean remove(final Object node) {
        if (node instanceof Node) {
            SimpleNode<T> nodeFixed = (SimpleNode<T>) node;

            if (hasMembership(nodeFixed) && canBeRemoved(nodeFixed)) {
                removeInternal(nodeFixed);

                return true;
            }
        }

        return false;
    }

    @Override
    public SimpleNode<T> removeFirst() {
        if (start.next == end) {
            return null;
        }

        return removeInternal(start.next);
    }

    @Override
    public SimpleNode<T> removeLast() {
        if (end.previous == start) {
            return null;
        }

        return removeInternal(end.previous);
    }

    @Override
    public void clear() {
        initialize();
    }

    @Override
    public Iterator<SimpleNode<T>> iterator() {
        return LinkedIterator.createStream(start.next, e -> e.next, e -> e != end)
                .iterator();
    }

    @Override
    public Iterator<SimpleNode<T>> descendingIterator() {
        return LinkedIterator.createStream(end.previous, e -> e.previous, e -> e != start)
                .iterator();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Membership implements Serializable {
        @Serial
        private static final long serialVersionUID = 5707462379223536762L;
    }
}
