package com.dipasquale.data.structure.set;

import com.dipasquale.data.structure.collection.AbstractCollection;
import com.dipasquale.data.structure.deque.Node;
import com.dipasquale.data.structure.deque.NodeDeque;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class AbstractDequeSet<TValue, TNode extends Node> extends AbstractCollection<TValue> implements DequeSet<TValue> {
    @Serial
    private static final long serialVersionUID = -8777988152140515009L;
    private final Map<TValue, TNode> nodesMap;
    private final NodeDeque<TValue, TNode> nodesDeque;

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

    private TValue get(final TValue value, final Navigator<TNode> nodeNavigator) {
        TNode node = nodesMap.get(value);

        if (node == null) {
            return null;
        }

        TNode otherNode = nodeNavigator.get(node);

        return nodesDeque.getValue(otherNode);
    }

    @Override
    public TValue getPrevious(final TValue value) {
        return get(value, nodesDeque::peekPrevious);
    }

    @Override
    public TValue getNext(final TValue value) {
        return get(value, nodesDeque::peekNext);
    }

    @Override
    public TValue getFirst() {
        return nodesDeque.getValue(nodesDeque.peekFirst());
    }

    @Override
    public TValue getLast() {
        return nodesDeque.getValue(nodesDeque.peekLast());
    }

    private boolean add(final TValue value, final Supplier<TNode> nodeSupplier, final Pairer<TNode> nodePairer) {
        TNode node = nodesMap.get(value);

        if (node == null) {
            TNode otherNode = nodeSupplier.get();

            if (otherNode != null) {
                node = nodesDeque.createUnbound(value);
                nodesMap.put(value, node);
                nodePairer.pair(node, otherNode);

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean addBefore(final TValue value, final TValue previousToValue) {
        return add(value, () -> nodesMap.get(previousToValue), nodesDeque::offerBefore);
    }

    @Override
    public boolean addAfter(final TValue value, final TValue nextToValue) {
        return add(value, () -> nodesMap.get(nextToValue), nodesDeque::offerAfter);
    }

    private boolean add(final TValue value, final Consumer<TNode> nodeConsumer) {
        TNode node = nodesMap.get(value);

        if (node != null) {
            return false;
        }

        node = nodesDeque.createUnbound(value);
        nodesMap.put(value, node);
        nodeConsumer.accept(node);

        return true;
    }

    @Override
    public boolean addFirst(final TValue value) {
        return add(value, nodesDeque::offerFirst);
    }

    @Override
    public boolean addLast(final TValue value) {
        return add(value, nodesDeque::offerLast);
    }

    @Override
    public boolean add(final TValue value) {
        return addLast(value);
    }

    @Override
    public boolean remove(final Object value) {
        Node node = nodesMap.remove(value);

        if (node == null) {
            return false;
        }

        nodesDeque.remove(node);

        return true;
    }

    private TValue remove(final TNode node) {
        if (node == null) {
            return null;
        }

        TValue value = nodesDeque.getValue(node);

        remove(value);

        return value;
    }

    private TValue remove(final TValue value, final Navigator<TNode> nodeNavigator) {
        TNode node = nodesMap.get(value);

        if (node == null) {
            return null;
        }

        return remove(nodeNavigator.get(node));
    }

    @Override
    public TValue removePrevious(final TValue previousToValue) {
        return remove(previousToValue, nodesDeque::peekPrevious);
    }

    @Override
    public TValue removeNext(final TValue nextToValue) {
        return remove(nextToValue, nodesDeque::peekNext);
    }

    @Override
    public TValue removeFirst() {
        return remove(nodesDeque.peekFirst());
    }

    @Override
    public TValue removeLast() {
        return remove(nodesDeque.peekLast());
    }

    @Override
    public void clear() {
        nodesMap.clear();
        nodesDeque.clear();
    }

    @Override
    public Iterator<TValue> iterator() {
        return nodesDeque.stream()
                .map(nodesDeque::getValue)
                .iterator();
    }

    @Override
    public Iterator<TValue> descendingIterator() {
        Iterable<TNode> iterable = nodesDeque::descendingIterator;

        return StreamSupport.stream(iterable.spliterator(), false)
                .map(nodesDeque::getValue)
                .iterator();
    }

    @FunctionalInterface
    private interface Navigator<T> {
        T get(T node);
    }

    @FunctionalInterface
    private interface Pairer<T> {
        void pair(T item1, T item2);
    }
}
