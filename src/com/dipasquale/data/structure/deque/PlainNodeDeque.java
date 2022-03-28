package com.dipasquale.data.structure.deque;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.data.structure.iterator.LinkedIterator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Iterator;

public final class PlainNodeDeque<T> extends AbstractDeque<PlainNode<T>> implements NodeDeque<T, PlainNode<T>>, Serializable {
    @Serial
    private static final long serialVersionUID = -6511910907907223574L;
    private Object membership;
    private PlainNode<T> start;
    private PlainNode<T> end;
    private int size;

    public PlainNodeDeque() {
        this.initialize();
    }

    private void initialize() {
        Object membership = new Membership();
        PlainNode<T> start = new PlainNode<>(membership, null);
        PlainNode<T> end = new PlainNode<>(membership, null);

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
    public PlainNode<T> createUnbound(final T value) {
        return new PlainNode<>(membership, value);
    }

    private boolean hasMembership(final PlainNode<T> node) {
        return node != null && node.membership == membership;
    }

    private static <T> boolean canBeAdded(final PlainNode<T> node) {
        return node.previous == null;
    }

    @Override
    public boolean contains(final Object node) {
        if (node instanceof PlainNode<?>) {
            PlainNode<T> fixedNode = (PlainNode<T>) node;

            return hasMembership(fixedNode) && !canBeAdded(fixedNode);
        }

        return false;
    }

    @Override
    public T getValue(final PlainNode<T> node) {
        if (!hasMembership(node)) {
            return null;
        }

        return node.value;
    }

    private PlainNode<T> peekPreviousInternal(final PlainNode<T> node) {
        if (node.previous == start) {
            return null;
        }

        return node.previous;
    }

    @Override
    public PlainNode<T> peekPrevious(final PlainNode<T> node) {
        if (!hasMembership(node)) {
            return null;
        }

        return peekPreviousInternal(node);
    }

    private PlainNode<T> peekNextInternal(final PlainNode<T> node) {
        if (node.next == end) {
            return null;
        }

        return node.next;
    }

    @Override
    public PlainNode<T> peekNext(final PlainNode<T> node) {
        if (!hasMembership(node)) {
            return null;
        }

        return peekNextInternal(node);
    }

    @Override
    public PlainNode<T> peekFirst() {
        return peekNextInternal(start);
    }

    @Override
    public PlainNode<T> peekLast() {
        return peekPreviousInternal(end);
    }

    private static <T> boolean canBeRemoved(final PlainNode<T> node) {
        return !canBeAdded(node);
    }

    private PlainNode<T> removeInternal(final PlainNode<T> node) {
        node.next.previous = node.previous;
        node.previous.next = node.next;
        node.previous = null;
        node.next = null;
        size--;

        return node;
    }

    private void offerBeforeInternal(final PlainNode<T> node, final PlainNode<T> previousToNode) {
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
    public boolean offerBefore(final PlainNode<T> node, final PlainNode<T> previousToNode) {
        if (!hasMembership(node) || !hasMembership(previousToNode)) {
            return false;
        }

        offerBeforeInternal(node, previousToNode);

        return true;
    }

    private void offerAfterInternal(final PlainNode<T> node, final PlainNode<T> nextToNode) {
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
    public boolean offerAfter(final PlainNode<T> node, final PlainNode<T> nextToNode) {
        if (!hasMembership(node) || !hasMembership(nextToNode)) {
            return false;
        }

        offerAfterInternal(node, nextToNode);

        return true;
    }

    @Override
    public boolean offerFirst(final PlainNode<T> node) {
        if (!hasMembership(node)) {
            return false;
        }

        offerAfterInternal(node, start);

        return true;
    }

    @Override
    public boolean offerLast(final PlainNode<T> node) {
        if (!hasMembership(node)) {
            return false;
        }

        offerBeforeInternal(node, end);

        return true;
    }

    private void ensureHasMembership(final PlainNode<T> node) {
        ArgumentValidatorSupport.ensureTrue(hasMembership(node), "node", "was not created by this deque");
    }

    @Override
    public void addBefore(final PlainNode<T> node, final PlainNode<T> previousToNode) {
        ensureHasMembership(node);
        ensureHasMembership(previousToNode);
        offerBefore(node, previousToNode);
    }

    @Override
    public void addAfter(final PlainNode<T> node, final PlainNode<T> nextToNode) {
        ensureHasMembership(node);
        ensureHasMembership(nextToNode);
        offerAfter(node, nextToNode);
    }

    @Override
    public void addFirst(final PlainNode<T> node) {
        ensureHasMembership(node);
        offerFirst(node);
    }

    @Override
    public void addLast(final PlainNode<T> node) {
        ensureHasMembership(node);
        offerLast(node);
    }

    @Override
    public boolean remove(final Object node) {
        if (node instanceof PlainNode<?>) {
            PlainNode<T> fixedNode = (PlainNode<T>) node;

            if (hasMembership(fixedNode) && canBeRemoved(fixedNode)) {
                removeInternal(fixedNode);

                return true;
            }
        }

        return false;
    }

    @Override
    public PlainNode<T> removeFirst() {
        if (start.next == end) {
            return null;
        }

        return removeInternal(start.next);
    }

    @Override
    public PlainNode<T> removeLast() {
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
    public Iterator<PlainNode<T>> iterator() {
        return LinkedIterator.createStream(start.next, node -> node.next, node -> node != end)
                .iterator();
    }

    @Override
    public Iterator<PlainNode<T>> descendingIterator() {
        return LinkedIterator.createStream(end.previous, node -> node.previous, node -> node != start)
                .iterator();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Membership implements Serializable {
        @Serial
        private static final long serialVersionUID = 5707462379223536762L;
    }
}
