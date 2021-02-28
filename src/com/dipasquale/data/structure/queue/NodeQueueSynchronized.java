package com.dipasquale.data.structure.queue;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Iterator;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NodeQueueSynchronized<T> implements NodeQueue<T> {
    private final NodeQueue<T> nodeQueue;

    @Override
    public Node createUnbound(final T value) {
        synchronized (nodeQueue) {
            return nodeQueue.createUnbound(value);
        }
    }

    @Override
    public int size() {
        synchronized (nodeQueue) {
            return nodeQueue.size();
        }
    }

    @Override
    public boolean isEmpty() { // NOTE: not originally implemented in NodeQueueDefault
        synchronized (nodeQueue) {
            return nodeQueue.isEmpty();
        }
    }

    @Override
    public boolean contains(final Object object) {
        synchronized (nodeQueue) {
            return nodeQueue.contains(object);
        }
    }

    @Override
    public boolean add(final Node node) {
        synchronized (nodeQueue) {
            return nodeQueue.add(node);
        }
    }

    @Override
    public boolean offer(final Node node) {
        synchronized (nodeQueue) {
            return nodeQueue.offer(node);
        }
    }

    @Override
    public boolean remove(final Object object) {
        synchronized (nodeQueue) {
            return nodeQueue.remove(object);
        }
    }

    @Override
    public Node remove() { // NOTE: not originally implemented in NodeQueueDefault
        synchronized (nodeQueue) {
            return nodeQueue.remove();
        }
    }

    @Override
    public boolean reoffer(final Node node) {
        synchronized (nodeQueue) {
            return nodeQueue.reoffer(node);
        }
    }

    @Override
    public Node poll() {
        synchronized (nodeQueue) {
            return nodeQueue.poll();
        }
    }

    @Override
    public Node element() { // NOTE: not originally implemented in NodeQueueDefault
        synchronized (nodeQueue) {
            return nodeQueue.element();
        }
    }

    @Override
    public Node peek() {
        synchronized (nodeQueue) {
            return nodeQueue.peek();
        }
    }

    @Override
    public Node first() {
        synchronized (nodeQueue) {
            return nodeQueue.first();
        }
    }

    @Override
    public Node last() {
        synchronized (nodeQueue) {
            return nodeQueue.last();
        }
    }

    @Override
    public Node previous(final Node node) {
        synchronized (nodeQueue) {
            return nodeQueue.previous(node);
        }
    }

    @Override
    public Node next(final Node node) {
        synchronized (nodeQueue) {
            return nodeQueue.next(node);
        }
    }

    @Override
    public T getValue(final Node node) {
        synchronized (nodeQueue) {
            return nodeQueue.getValue(node);
        }
    }

    @Override
    public void clear() {
        synchronized (nodeQueue) {
            nodeQueue.clear();
        }
    }

    @Override
    public Iterator<Node> iterator() {
        synchronized (nodeQueue) {
            return nodeQueue.iterator();
        }
    }

    @Override
    public Iterator<Node> iteratorDescending() {
        synchronized (nodeQueue) {
            return nodeQueue.iteratorDescending();
        }
    }

    @Override
    public Object[] toArray() { // NOTE: not originally implemented in NodeQueueDefault
        synchronized (nodeQueue) {
            return nodeQueue.toArray();
        }
    }

    @Override
    public <R> R[] toArray(final R[] array) { // NOTE: not originally implemented in NodeQueueDefault
        synchronized (nodeQueue) {
            return nodeQueue.toArray(array);
        }
    }

    @Override
    public boolean containsAll(final Collection<?> collection) { // NOTE: not originally implemented in NodeQueueDefault
        synchronized (nodeQueue) {
            return nodeQueue.containsAll(collection);
        }
    }

    @Override
    public boolean addAll(final Collection<? extends Node> collection) { // NOTE: not originally implemented in NodeQueueDefault
        synchronized (nodeQueue) {
            return nodeQueue.addAll(collection);
        }
    }

    @Override
    public boolean retainAll(final Collection<?> collection) {
        synchronized (nodeQueue) {
            return nodeQueue.retainAll(collection);
        }
    }

    @Override
    public boolean removeAll(final Collection<?> collection) {
        synchronized (nodeQueue) {
            return nodeQueue.removeAll(collection);
        }
    }
}
