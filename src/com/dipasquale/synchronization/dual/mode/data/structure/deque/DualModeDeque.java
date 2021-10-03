package com.dipasquale.synchronization.dual.mode.data.structure.deque;

import com.dipasquale.common.factory.data.structure.deque.DequeFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class DualModeDeque<TItem, TDequeFactory extends DequeFactory & DualModeObject> implements Deque<TItem>, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = -6500514609593480259L;
    private final TDequeFactory dequeFactory;
    private Deque<TItem> deque;

    public DualModeDeque(final TDequeFactory dequeFactory) {
        this.dequeFactory = dequeFactory;
        this.deque = dequeFactory.create(null);
    }

    @Override
    public int size() {
        return deque.size();
    }

    @Override
    public boolean isEmpty() {
        return deque.isEmpty();
    }

    @Override
    public boolean contains(final Object object) {
        return deque.contains(object);
    }

    @Override
    public boolean containsAll(final Collection<?> collection) {
        return deque.containsAll(collection);
    }

    @Override
    public TItem peek() {
        return deque.peek();
    }

    @Override
    public TItem peekFirst() {
        return deque.peekFirst();
    }

    @Override
    public TItem peekLast() {
        return deque.peekLast();
    }

    @Override
    public TItem element() {
        return deque.element();
    }

    @Override
    public TItem getFirst() {
        return deque.getFirst();
    }

    @Override
    public TItem getLast() {
        return deque.getLast();
    }

    @Override
    public boolean offer(final TItem value) {
        return deque.offer(value);
    }

    @Override
    public boolean offerFirst(final TItem value) {
        return deque.offerFirst(value);
    }

    @Override
    public boolean offerLast(final TItem value) {
        return deque.offerLast(value);
    }

    @Override
    public boolean add(final TItem value) {
        return deque.add(value);
    }

    @Override
    public void addFirst(final TItem value) {
        deque.addFirst(value);
    }

    @Override
    public void addLast(final TItem value) {
        deque.addLast(value);
    }

    @Override
    public boolean addAll(final Collection<? extends TItem> collection) {
        return deque.addAll(collection);
    }

    @Override
    public void push(final TItem value) {
        deque.push(value);
    }

    @Override
    public boolean remove(final Object object) {
        return deque.remove(object);
    }

    @Override
    public TItem remove() {
        return deque.remove();
    }

    @Override
    public TItem removeFirst() {
        return deque.removeFirst();
    }

    @Override
    public TItem removeLast() {
        return deque.removeLast();
    }

    @Override
    public TItem poll() {
        return deque.poll();
    }

    @Override
    public TItem pop() {
        return deque.pop();
    }

    @Override
    public TItem pollFirst() {
        return deque.pollFirst();
    }

    @Override
    public TItem pollLast() {
        return deque.pollLast();
    }

    @Override
    public boolean removeFirstOccurrence(final Object object) {
        return deque.removeFirstOccurrence(object);
    }

    @Override
    public boolean removeLastOccurrence(final Object object) {
        return deque.removeLastOccurrence(object);
    }

    @Override
    public boolean removeAll(final Collection<?> collection) {
        return deque.removeAll(collection);
    }

    @Override
    public boolean removeIf(final Predicate<? super TItem> filter) {
        return deque.removeIf(filter);
    }

    @Override
    public boolean retainAll(final Collection<?> collection) {
        return deque.retainAll(collection);
    }

    @Override
    public void clear() {
        deque.clear();
    }

    @Override
    public Spliterator<TItem> spliterator() {
        return deque.spliterator();
    }

    @Override
    public Stream<TItem> stream() {
        return deque.stream();
    }

    @Override
    public Stream<TItem> parallelStream() {
        return deque.parallelStream();
    }

    @Override
    public Iterator<TItem> iterator() {
        return deque.iterator();
    }

    @Override
    public void forEach(final Consumer<? super TItem> action) {
        deque.forEach(action);
    }

    @Override
    public Object[] toArray() {
        return deque.toArray();
    }

    @Override
    public <TArray> TArray[] toArray(final TArray[] array) {
        return deque.toArray(array);
    }

    @Override
    public <TArray> TArray[] toArray(final IntFunction<TArray[]> generator) {
        return deque.toArray(generator);
    }

    @Override
    public Iterator<TItem> descendingIterator() {
        return deque.descendingIterator();
    }

    @Override
    public int concurrencyLevel() {
        return dequeFactory.concurrencyLevel();
    }

    @Override
    public void activateMode(final int concurrencyLevel) {
        dequeFactory.activateMode(concurrencyLevel);
        deque = dequeFactory.create(deque);
    }
}
