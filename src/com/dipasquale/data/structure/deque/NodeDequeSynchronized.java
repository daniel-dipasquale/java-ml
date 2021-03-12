package com.dipasquale.data.structure.deque;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.IntFunction;
import java.util.function.Predicate;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class NodeDequeSynchronized<T> implements NodeDeque<T> {
    private final NodeDeque<T> nodeDeque;

    @Override
    public Node createUnbound(final T value) {
        synchronized (nodeDeque) {
            return nodeDeque.createUnbound(value);
        }
    }

    @Override
    public T getValue(final Node node) {
        synchronized (nodeDeque) {
            return nodeDeque.getValue(node);
        }
    }

    @Override
    public int size() {
        synchronized (nodeDeque) {
            return nodeDeque.size();
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (nodeDeque) {
            return nodeDeque.isEmpty();
        }
    }

    @Override
    public boolean contains(final Object node) {
        synchronized (nodeDeque) {
            return nodeDeque.contains(node);
        }
    }

    @Override
    public Node peekPrevious(final Node node) {
        synchronized (nodeDeque) {
            return nodeDeque.peekPrevious(node);
        }
    }

    @Override
    public Node peekNext(final Node node) {
        synchronized (nodeDeque) {
            return nodeDeque.peekNext(node);
        }
    }

    @Override
    public Node peekFirst() {
        synchronized (nodeDeque) {
            return nodeDeque.peekFirst();
        }
    }

    @Override
    public Node peekLast() {
        synchronized (nodeDeque) {
            return nodeDeque.peekLast();
        }
    }

    @Override
    public Node peek() {
        synchronized (nodeDeque) {
            return nodeDeque.peek();
        }
    }

    @Override
    public Node getFirst() {
        synchronized (nodeDeque) {
            return nodeDeque.getFirst();
        }
    }

    @Override
    public Node getLast() {
        synchronized (nodeDeque) {
            return nodeDeque.getLast();
        }
    }

    @Override
    public Node element() {
        synchronized (nodeDeque) {
            return nodeDeque.element();
        }
    }

    @Override
    public boolean offer(final Node node) {
        synchronized (nodeDeque) {
            return nodeDeque.offer(node);
        }
    }

    @Override
    public boolean offerBefore(final Node node, final Node previousToNode) {
        synchronized (nodeDeque) {
            return nodeDeque.offerBefore(node, previousToNode);
        }
    }

    @Override
    public boolean offerAfter(final Node node, final Node nextToNode) {
        synchronized (nodeDeque) {
            return nodeDeque.offerAfter(node, nextToNode);
        }
    }

    @Override
    public boolean offerFirst(final Node node) {
        synchronized (nodeDeque) {
            return nodeDeque.offerFirst(node);
        }
    }

    @Override
    public boolean offerLast(final Node node) {
        synchronized (nodeDeque) {
            return nodeDeque.offerLast(node);
        }
    }

    @Override
    public boolean add(final Node node) {
        synchronized (nodeDeque) {
            return nodeDeque.add(node);
        }
    }

    @Override
    public void addBefore(final Node node, final Node previousToNode) {
        synchronized (nodeDeque) {
            nodeDeque.addBefore(node, previousToNode);
        }
    }

    @Override
    public void addAfter(final Node node, final Node nextToNode) {
        synchronized (nodeDeque) {
            nodeDeque.addAfter(node, nextToNode);
        }
    }

    @Override
    public void addFirst(final Node node) {
        synchronized (nodeDeque) {
            nodeDeque.addFirst(node);
        }
    }

    @Override
    public void addLast(final Node node) {
        synchronized (nodeDeque) {
            nodeDeque.addLast(node);
        }
    }

    @Override
    public void push(final Node node) {
        synchronized (nodeDeque) {
            nodeDeque.push(node);
        }
    }

    @Override
    public boolean remove(final Object node) {
        synchronized (nodeDeque) {
            return nodeDeque.remove(node);
        }
    }

    @Override
    public Node removeFirst() {
        synchronized (nodeDeque) {
            return nodeDeque.removeFirst();
        }
    }

    @Override
    public Node removeLast() {
        synchronized (nodeDeque) {
            return nodeDeque.removeLast();
        }
    }

    @Override
    public Node remove() {
        synchronized (nodeDeque) {
            return nodeDeque.remove();
        }
    }

    @Override
    public Node pollFirst() {
        synchronized (nodeDeque) {
            return nodeDeque.pollFirst();
        }
    }

    @Override
    public Node pollLast() {
        synchronized (nodeDeque) {
            return nodeDeque.pollLast();
        }
    }

    @Override
    public Node poll() {
        synchronized (nodeDeque) {
            return nodeDeque.poll();
        }
    }

    @Override
    public Node pop() {
        synchronized (nodeDeque) {
            return nodeDeque.pop();
        }
    }

    @Override
    public boolean removeFirstOccurrence(final Object node) {
        synchronized (nodeDeque) {
            return nodeDeque.removeFirstOccurrence(node);
        }
    }

    @Override
    public boolean removeLastOccurrence(final Object node) {
        synchronized (nodeDeque) {
            return nodeDeque.removeLastOccurrence(node);
        }
    }

    @Override
    public boolean removeIf(final Predicate<? super Node> filter) {
        synchronized (nodeDeque) {
            return nodeDeque.removeIf(filter);
        }
    }

    @Override
    public void clear() {
        synchronized (nodeDeque) {
            nodeDeque.clear();
        }
    }

    @Override
    public Iterator<Node> iterator() {
        synchronized (nodeDeque) {
            return nodeDeque.iterator();
        }
    }

    @Override
    public Iterator<Node> descendingIterator() {
        return nodeDeque.descendingIterator();
    }

    @Override
    public boolean containsAll(final Collection<?> collection) {
        synchronized (nodeDeque) {
            return nodeDeque.containsAll(collection);
        }
    }

    @Override
    public boolean addAll(final Collection<? extends Node> collection) {
        synchronized (nodeDeque) {
            return nodeDeque.addAll(collection);
        }
    }

    @Override
    public boolean removeAll(final Collection<?> collection) {
        synchronized (nodeDeque) {
            return nodeDeque.removeAll(collection);
        }
    }

    @Override
    public boolean retainAll(final Collection<?> collection) {
        synchronized (nodeDeque) {
            return nodeDeque.retainAll(collection);
        }
    }

    @Override
    public Object[] toArray() {
        synchronized (nodeDeque) {
            return nodeDeque.toArray();
        }
    }

    @Override
    public <R> R[] toArray(final R[] array) {
        synchronized (nodeDeque) {
            return nodeDeque.toArray(array);
        }
    }

    @Override
    public <R> R[] toArray(final IntFunction<R[]> generator) {
        synchronized (nodeDeque) {
            return nodeDeque.toArray(generator);
        }
    }

    @Override
    public boolean equals(final Object other) {
        synchronized (nodeDeque) {
            return nodeDeque.equals(other);
        }
    }

    @Override
    public int hashCode() {
        synchronized (nodeDeque) {
            return nodeDeque.hashCode();
        }
    }
}
