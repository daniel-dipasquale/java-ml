package com.dipasquale.data.structure.queue;

import java.util.Iterator;
import java.util.Queue;

public interface NodeQueue<T> extends Queue<Node> {
    Node createUnbound(T value);

    boolean reoffer(Node node);

    Node first();

    @Override
    default Node peek() {
        return first();
    }

    Node last();

    Node previous(Node node);

    Node next(Node node);

    T getValue(Node node);

    Iterator<Node> iteratorDescending();

    static <T> NodeQueue<T> create() {
        return new NodeQueueDefault<>();
    }

    static <T> NodeQueue<T> createSynchronized() {
        NodeQueue<T> queue = create();

        return new NodeQueueSynchronized<>(queue);
    }
}
