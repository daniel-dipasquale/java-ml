package com.dipasquale.data.structure.set;

import com.dipasquale.data.structure.deque.Node;
import com.dipasquale.data.structure.deque.NodeDeque;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class DequeSetDefault<T> implements DequeSet<T> {
    private final Map<T, Node> nodesMap;
    private final NodeDeque<T> nodesQueue;

    @Override
    public int size() {
        return nodesMap.size();
    }

    @Override
    public boolean isEmpty() {
        return nodesMap.isEmpty();
    }

    @Override
    public boolean contains(final Object value) {
        return nodesMap.containsKey(value);
    }

    private T get(final T value, final NodeGetter nodeGetter) {
        Node node = nodesMap.get(value);

        if (node == null) {
            return null;
        }

        Node otherNode = nodeGetter.get(node);

        return nodesQueue.getValue(otherNode);
    }

    @Override
    public T getPrevious(final T value) {
        return get(value, nodesQueue::peekPrevious);
    }

    @Override
    public T getNext(final T value) {
        return get(value, nodesQueue::peekNext);
    }

    @Override
    public T getFirst() {
        return nodesQueue.getValue(nodesQueue.peekFirst());
    }

    @Override
    public T getLast() {
        return nodesQueue.getValue(nodesQueue.peekLast());
    }

    private boolean add(final T value, final NodeSupplier nodeSupplier, final NodePairer nodePairer) {
        Node node = nodesMap.get(value);

        if (node == null) {
            Node otherNode = nodeSupplier.get();

            if (otherNode != null) {
                node = nodesQueue.createUnbound(value);
                nodesMap.put(value, node);
                nodePairer.pair(node, otherNode);

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean addBefore(final T value, final T previousToValue) {
        return add(value, () -> nodesMap.get(previousToValue), nodesQueue::offerBefore);
    }

    @Override
    public boolean addAfter(final T value, final T nextToValue) {
        return add(value, () -> nodesMap.get(nextToValue), nodesQueue::offerAfter);
    }

    private boolean add(final T value, final NodeConsumer nodeConsumer) {
        Node node = nodesMap.get(value);

        if (node != null) {
            return false;
        }

        node = nodesQueue.createUnbound(value);
        nodesMap.put(value, node);
        nodeConsumer.accept(node);

        return true;
    }

    @Override
    public boolean addFirst(final T value) {
        return add(value, nodesQueue::offerFirst);
    }

    @Override
    public boolean addLast(final T value) {
        return add(value, nodesQueue::offerLast);
    }

    @Override
    public boolean remove(final Object value) {
        Node node = nodesMap.remove(value);

        if (node == null) {
            return false;
        }

        nodesQueue.remove(node);

        return true;
    }

    private T remove(final Node node) {
        if (node == null) {
            return null;
        }

        T value = nodesQueue.getValue(node);

        remove(value);

        return value;
    }

    private T remove(final T value, final NodeGetter nodeGetter) {
        Node node = nodesMap.get(value);

        if (node == null) {
            return null;
        }

        return remove(nodeGetter.get(node));
    }

    @Override
    public T removePrevious(final T previousToValue) {
        return remove(previousToValue, nodesQueue::peekPrevious);
    }

    @Override
    public T removeNext(final T nextToValue) {
        return remove(nextToValue, nodesQueue::peekNext);
    }

    @Override
    public T removeFirst() {
        return remove(nodesQueue.peekFirst());
    }

    @Override
    public T removeLast() {
        return remove(nodesQueue.peekLast());
    }

    @Override
    public void clear() {
        nodesMap.clear();
        nodesQueue.clear();
    }

    @Override
    public Iterator<T> iterator() {
        return nodesQueue.stream()
                .map(nodesQueue::getValue)
                .iterator();
    }

    @Override
    public Iterator<T> descendingIterator() {
        Iterable<Node> iterable = nodesQueue::descendingIterator;

        return StreamSupport.stream(iterable.spliterator(), false)
                .map(nodesQueue::getValue)
                .iterator();
    }

    @FunctionalInterface
    private interface NodeGetter {
        Node get(Node node);
    }

    @FunctionalInterface
    private interface NodeSupplier {
        Node get();
    }

    @FunctionalInterface
    private interface NodePairer {
        void pair(Node node1, Node node2);
    }

    @FunctionalInterface
    private interface NodeConsumer {
        void accept(Node node);
    }
}
