package com.dipasquale.data.structure.deque.concurrent;

import com.dipasquale.data.structure.deque.Node;
import com.dipasquale.data.structure.deque.NodeDeque;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.IntFunction;
import java.util.function.Predicate;

@RequiredArgsConstructor
public final class SynchronizedNodeDeque<TValue, TNode extends Node> implements NodeDeque<TValue, TNode>, Serializable {
    @Serial
    private static final long serialVersionUID = -4409715583250590379L;
    private final NodeDeque<TValue, TNode> nodeDeque;

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
    public TNode createUnbound(final TValue value) {
        synchronized (nodeDeque) {
            return nodeDeque.createUnbound(value);
        }
    }

    @Override
    public boolean contains(final Object node) {
        synchronized (nodeDeque) {
            return nodeDeque.contains(node);
        }
    }

    @Override
    public TValue getValue(final TNode node) {
        synchronized (nodeDeque) {
            return nodeDeque.getValue(node);
        }
    }

    @Override
    public TNode peekPrevious(final TNode node) {
        synchronized (nodeDeque) {
            return nodeDeque.peekPrevious(node);
        }
    }

    @Override
    public TNode peekNext(final TNode node) {
        synchronized (nodeDeque) {
            return nodeDeque.peekNext(node);
        }
    }

    @Override
    public TNode peekFirst() {
        synchronized (nodeDeque) {
            return nodeDeque.peekFirst();
        }
    }

    @Override
    public TNode peekLast() {
        synchronized (nodeDeque) {
            return nodeDeque.peekLast();
        }
    }

    @Override
    public TNode peek() {
        synchronized (nodeDeque) {
            return nodeDeque.peek();
        }
    }

    @Override
    public TNode getFirst() {
        synchronized (nodeDeque) {
            return nodeDeque.getFirst();
        }
    }

    @Override
    public TNode getLast() {
        synchronized (nodeDeque) {
            return nodeDeque.getLast();
        }
    }

    @Override
    public TNode element() {
        synchronized (nodeDeque) {
            return nodeDeque.element();
        }
    }

    @Override
    public boolean offer(final TNode node) {
        synchronized (nodeDeque) {
            return nodeDeque.offer(node);
        }
    }

    @Override
    public boolean offerBefore(final TNode node, final TNode previousToNode) {
        synchronized (nodeDeque) {
            return nodeDeque.offerBefore(node, previousToNode);
        }
    }

    @Override
    public boolean offerAfter(final TNode node, final TNode nextToNode) {
        synchronized (nodeDeque) {
            return nodeDeque.offerAfter(node, nextToNode);
        }
    }

    @Override
    public boolean offerFirst(final TNode node) {
        synchronized (nodeDeque) {
            return nodeDeque.offerFirst(node);
        }
    }

    @Override
    public boolean offerLast(final TNode node) {
        synchronized (nodeDeque) {
            return nodeDeque.offerLast(node);
        }
    }

    @Override
    public boolean add(final TNode node) {
        synchronized (nodeDeque) {
            return nodeDeque.add(node);
        }
    }

    @Override
    public void addBefore(final TNode node, final TNode previousToNode) {
        synchronized (nodeDeque) {
            nodeDeque.addBefore(node, previousToNode);
        }
    }

    @Override
    public void addAfter(final TNode node, final TNode nextToNode) {
        synchronized (nodeDeque) {
            nodeDeque.addAfter(node, nextToNode);
        }
    }

    @Override
    public void addFirst(final TNode node) {
        synchronized (nodeDeque) {
            nodeDeque.addFirst(node);
        }
    }

    @Override
    public void addLast(final TNode node) {
        synchronized (nodeDeque) {
            nodeDeque.addLast(node);
        }
    }

    @Override
    public void push(final TNode node) {
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
    public TNode removeFirst() {
        synchronized (nodeDeque) {
            return nodeDeque.removeFirst();
        }
    }

    @Override
    public TNode removeLast() {
        synchronized (nodeDeque) {
            return nodeDeque.removeLast();
        }
    }

    @Override
    public TNode remove() {
        synchronized (nodeDeque) {
            return nodeDeque.remove();
        }
    }

    @Override
    public TNode pollFirst() {
        synchronized (nodeDeque) {
            return nodeDeque.pollFirst();
        }
    }

    @Override
    public TNode pollLast() {
        synchronized (nodeDeque) {
            return nodeDeque.pollLast();
        }
    }

    @Override
    public TNode poll() {
        synchronized (nodeDeque) {
            return nodeDeque.poll();
        }
    }

    @Override
    public TNode pop() {
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
    public boolean removeIf(final Predicate<? super TNode> filter) {
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
    public Iterator<TNode> iterator() {
        synchronized (nodeDeque) {
            return nodeDeque.iterator();
        }
    }

    @Override
    public Iterator<TNode> descendingIterator() {
        return nodeDeque.descendingIterator();
    }

    @Override
    public boolean containsAll(final Collection<?> collection) {
        synchronized (nodeDeque) {
            return nodeDeque.containsAll(collection);
        }
    }

    @Override
    public boolean addAll(final Collection<? extends TNode> collection) {
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
    public <TArray> TArray[] toArray(final TArray[] array) {
        synchronized (nodeDeque) {
            return nodeDeque.toArray(array);
        }
    }

    @Override
    public <TArray> TArray[] toArray(final IntFunction<TArray[]> generator) {
        synchronized (nodeDeque) {
            return nodeDeque.toArray(generator);
        }
    }

    @Override
    public boolean equals(final Object other) {
        return nodeDeque.equals(other);
    }

    @Override
    public int hashCode() {
        return nodeDeque.hashCode();
    }

    @Override
    public String toString() {
        return nodeDeque.toString();
    }
}
