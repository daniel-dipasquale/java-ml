package com.dipasquale.data.structure.deque;

import java.util.Deque;

public interface NodeDeque<T> extends Deque<Node> {
    Node createUnbound(T value);

    T getValue(Node node);

    Node peekPrevious(Node node);

    Node peekNext(Node node);

    boolean offerBefore(Node node, Node previousToNode);

    boolean offerAfter(Node node, Node nextToNode);

    void addBefore(Node node, Node previousToNode);

    void addAfter(Node node, Node nextToNode);

    static <T> NodeDeque<T> create() {
        return new NodeDequeDefault<>();
    }

    static <T> NodeDeque<T> createSynchronized() {
        NodeDeque<T> queue = create();

        return new NodeDequeSynchronized<>(queue);
    }
}
