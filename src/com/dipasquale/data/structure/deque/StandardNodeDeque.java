package com.dipasquale.data.structure.deque;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.data.structure.iterator.LinkedIterator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Iterator;

public final class StandardNodeDeque<T> extends AbstractDeque<StandardNode<T>> implements NodeDeque<T, StandardNode<T>>, Serializable {
    @Serial
    private static final long serialVersionUID = -6511910907907223574L;
    private Object membership;
    private StandardNode<T> start;
    private StandardNode<T> end;
    private int size;

    public StandardNodeDeque() {
        this.initialize();
    }

    private void initialize() {
        Object membership = new Membership();
        StandardNode<T> start = new StandardNode<>(membership, null);
        StandardNode<T> end = new StandardNode<>(membership, null);

        start.next = end;
        end.previous = start;
        this.membership = membership;
        this.start = start;
        this.end = end;
        this.size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public StandardNode<T> createUnbound(final T value) {
        return new StandardNode<>(membership, value);
    }

    private boolean hasMembership(final StandardNode<T> node) {
        return node != null && node.membership == membership;
    }

    private static <T> boolean canBeAdded(final StandardNode<T> node) {
        return node.previous == null;
    }

    @Override
    public boolean contains(final Object node) {
        if (node instanceof StandardNode<?>) {
            StandardNode<T> fixedNode = (StandardNode<T>) node;

            return hasMembership(fixedNode) && !canBeAdded(fixedNode);
        }

        return false;
    }

    @Override
    public T getValue(final StandardNode<T> node) {
        if (!hasMembership(node)) {
            return null;
        }

        return node.value;
    }

    private StandardNode<T> peekPreviousInternal(final StandardNode<T> node) {
        if (node.previous == start) {
            return null;
        }

        return node.previous;
    }

    @Override
    public StandardNode<T> peekPrevious(final StandardNode<T> node) {
        if (!hasMembership(node)) {
            return null;
        }

        return peekPreviousInternal(node);
    }

    private StandardNode<T> peekNextInternal(final StandardNode<T> node) {
        if (node.next == end) {
            return null;
        }

        return node.next;
    }

    @Override
    public StandardNode<T> peekNext(final StandardNode<T> node) {
        if (!hasMembership(node)) {
            return null;
        }

        return peekNextInternal(node);
    }

    @Override
    public StandardNode<T> peekFirst() {
        return peekNextInternal(start);
    }

    @Override
    public StandardNode<T> peekLast() {
        return peekPreviousInternal(end);
    }

    private static <T> boolean canBeRemoved(final StandardNode<T> node) {
        return !canBeAdded(node);
    }

    private StandardNode<T> removeInternal(final StandardNode<T> node) {
        node.next.previous = node.previous;
        node.previous.next = node.next;
        node.previous = null;
        node.next = null;
        size--;

        return node;
    }

    private void offerBeforeInternal(final StandardNode<T> node, final StandardNode<T> previousToNode) {
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
    public boolean offerBefore(final StandardNode<T> node, final StandardNode<T> previousToNode) {
        if (!hasMembership(node) || !hasMembership(previousToNode)) {
            return false;
        }

        offerBeforeInternal(node, previousToNode);

        return true;
    }

    private void offerAfterInternal(final StandardNode<T> node, final StandardNode<T> nextToNode) {
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
    public boolean offerAfter(final StandardNode<T> node, final StandardNode<T> nextToNode) {
        if (!hasMembership(node) || !hasMembership(nextToNode)) {
            return false;
        }

        offerAfterInternal(node, nextToNode);

        return true;
    }

    @Override
    public boolean offerFirst(final StandardNode<T> node) {
        if (!hasMembership(node)) {
            return false;
        }

        offerAfterInternal(node, start);

        return true;
    }

    @Override
    public boolean offerLast(final StandardNode<T> node) {
        if (!hasMembership(node)) {
            return false;
        }

        offerBeforeInternal(node, end);

        return true;
    }

    private void ensureHasMembership(final StandardNode<T> node) {
        ArgumentValidatorSupport.ensureTrue(hasMembership(node), "node", "was not created by this deque");
    }

    @Override
    public void addBefore(final StandardNode<T> node, final StandardNode<T> previousToNode) {
        ensureHasMembership(node);
        ensureHasMembership(previousToNode);
        offerBefore(node, previousToNode);
    }

    @Override
    public void addAfter(final StandardNode<T> node, final StandardNode<T> nextToNode) {
        ensureHasMembership(node);
        ensureHasMembership(nextToNode);
        offerAfter(node, nextToNode);
    }

    @Override
    public void addFirst(final StandardNode<T> node) {
        ensureHasMembership(node);
        offerFirst(node);
    }

    @Override
    public void addLast(final StandardNode<T> node) {
        ensureHasMembership(node);
        offerLast(node);
    }

    @Override
    public boolean remove(final Object node) {
        if (node instanceof StandardNode<?>) {
            StandardNode<T> fixedNode = (StandardNode<T>) node;

            if (hasMembership(fixedNode) && canBeRemoved(fixedNode)) {
                removeInternal(fixedNode);

                return true;
            }
        }

        return false;
    }

    @Override
    public StandardNode<T> removeFirst() {
        if (start.next == end) {
            return null;
        }

        return removeInternal(start.next);
    }

    @Override
    public StandardNode<T> removeLast() {
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
    public Iterator<StandardNode<T>> iterator() {
        return LinkedIterator.createStream(start.next, node -> node.next, node -> node != end)
                .iterator();
    }

    @Override
    public Iterator<StandardNode<T>> descendingIterator() {
        return LinkedIterator.createStream(end.previous, node -> node.previous, node -> node != start)
                .iterator();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Membership implements Serializable {
        @Serial
        private static final long serialVersionUID = 5707462379223536762L;
    }
}
