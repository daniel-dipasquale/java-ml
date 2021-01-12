package com.dipasquale.data.structure.queue;

import java.util.Iterator;
import java.util.Queue;

public interface NodeQueue<T> extends Queue<Node> {
    Node createUnlinked(T value);

    boolean reoffer(Node node);

    Node first();

    Node last();

    Node previous(Node node);

    Node next(Node node);

    T getValue(Node node);

    Iterator<Node> iteratorDescending();

    static <T> NodeQueue<T> create() {
        return new NodeQueueDefault<>();
    }
}
