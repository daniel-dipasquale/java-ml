package com.dipasquale.data.structure.deque;

import java.util.Deque;
import java.util.NoSuchElementException;

public interface NodeDeque<T> extends Deque<Node> {
    Node createUnbound(T value);

    @Override
    default Node peek() {
        return peekFirst();
    }

    @Override
    default Node element() {
        Node node = peekFirst();

        if (node == null) {
            throw new NoSuchElementException("the node dequeue is empty");
        }

        return node;
    }

    Node peekPrevious(Node node);

    Node peekNext(Node node);

    T getValue(Node node);

    @Override
    default boolean add(final Node node) {
        addLast(node);

        return true;
    }

    @Override
    default void push(final Node node) {
        offerFirst(node);
    }

    @Override
    default boolean offer(final Node node) {
        return offerLast(node);
    }

    @Override
    default Node getFirst() {
        return peekFirst();
    }

    @Override
    default Node getLast() {
        return peekLast();
    }

    @Override
    default Node remove() {
        return removeFirst();
    }

    @Override
    default Node pollFirst() {
        return removeFirst();
    }

    @Override
    default Node pollLast() {
        return removeLast();
    }

    @Override
    default Node poll() {
        return removeFirst();
    }

    @Override
    default Node pop() {
        return removeLast();
    }

    @Override
    default boolean removeFirstOccurrence(final Object node) {
        return remove(node);
    }

    @Override
    default boolean removeLastOccurrence(final Object node) {
        return remove(node);
    }

    static <T> NodeDeque<T> create() {
        return new NodeDequeDefault<>();
    }

    static <T> NodeDeque<T> createSynchronized() {
        NodeDeque<T> queue = create();

        return new NodeDequeSynchronized<>(queue);
    }
}
