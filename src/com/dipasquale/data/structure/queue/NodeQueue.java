package com.dipasquale.data.structure.queue;

import java.util.Collection;
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

    private static <T> NodeQueue<T> createSynchronized(final NodeQueue<T> queue) {
        return new NodeQueue<>() {
            @Override
            public Node createUnbound(final T value) {
                synchronized (queue) {
                    return queue.createUnbound(value);
                }
            }

            @Override
            public int size() {
                synchronized (queue) {
                    return queue.size();
                }
            }

            @Override
            public boolean isEmpty() { // NOTE: not originally implemented in NodeQueueDefault
                synchronized (queue) {
                    return queue.isEmpty();
                }
            }

            @Override
            public boolean contains(final Object object) {
                synchronized (queue) {
                    return queue.contains(object);
                }
            }

            @Override
            public boolean add(final Node node) {
                synchronized (queue) {
                    return queue.add(node);
                }
            }

            @Override
            public boolean offer(final Node node) {
                synchronized (queue) {
                    return queue.offer(node);
                }
            }

            @Override
            public boolean remove(final Object object) {
                synchronized (queue) {
                    return queue.remove(object);
                }
            }

            @Override
            public Node remove() { // NOTE: not originally implemented in NodeQueueDefault
                synchronized (queue) {
                    return queue.remove();
                }
            }

            @Override
            public boolean reoffer(final Node node) {
                synchronized (queue) {
                    return queue.reoffer(node);
                }
            }

            @Override
            public Node poll() {
                synchronized (queue) {
                    return queue.poll();
                }
            }

            @Override
            public Node element() { // NOTE: not originally implemented in NodeQueueDefault
                synchronized (queue) {
                    return queue.element();
                }
            }

            @Override
            public Node peek() {
                synchronized (queue) {
                    return queue.peek();
                }
            }

            @Override
            public Node first() {
                synchronized (queue) {
                    return queue.first();
                }
            }

            @Override
            public Node last() {
                synchronized (queue) {
                    return queue.last();
                }
            }

            @Override
            public Node previous(final Node node) {
                synchronized (queue) {
                    return queue.previous(node);
                }
            }

            @Override
            public Node next(final Node node) {
                synchronized (queue) {
                    return queue.next(node);
                }
            }

            @Override
            public T getValue(final Node node) {
                synchronized (queue) {
                    return queue.getValue(node);
                }
            }

            @Override
            public void clear() {
                synchronized (queue) {
                    queue.clear();
                }
            }

            @Override
            public Iterator<Node> iterator() {
                synchronized (queue) {
                    return queue.iterator();
                }
            }

            @Override
            public Iterator<Node> iteratorDescending() {
                synchronized (queue) {
                    return queue.iteratorDescending();
                }
            }

            @Override
            public Object[] toArray() { // NOTE: not originally implemented in NodeQueueDefault
                synchronized (queue) {
                    return queue.toArray();
                }
            }

            @Override
            public <R> R[] toArray(final R[] array) { // NOTE: not originally implemented in NodeQueueDefault
                synchronized (queue) {
                    return queue.toArray(array);
                }
            }

            @Override
            public boolean containsAll(final Collection<?> collection) { // NOTE: not originally implemented in NodeQueueDefault
                synchronized (queue) {
                    return queue.containsAll(collection);
                }
            }

            @Override
            public boolean addAll(final Collection<? extends Node> collection) { // NOTE: not originally implemented in NodeQueueDefault
                synchronized (queue) {
                    return queue.addAll(collection);
                }
            }

            @Override
            public boolean retainAll(final Collection<?> collection) {
                synchronized (queue) {
                    return queue.retainAll(collection);
                }
            }

            @Override
            public boolean removeAll(final Collection<?> collection) {
                synchronized (queue) {
                    return queue.removeAll(collection);
                }
            }
        };
    }

    static <T> NodeQueue<T> createSynchronized() {
        NodeQueue<T> queue = create();

        return createSynchronized(queue);
    }
}
