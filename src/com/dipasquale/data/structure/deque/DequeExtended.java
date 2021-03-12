package com.dipasquale.data.structure.deque;

import com.dipasquale.data.structure.collection.CollectionExtensions;

import java.util.Collection;
import java.util.Deque;
import java.util.function.IntFunction;
import java.util.function.Predicate;

public interface DequeExtended<T> extends Deque<T> {
    @Override
    default boolean isEmpty() {
        return CollectionExtensions.isEmpty(this);
    }

    @Override
    default T peek() {
        return DequeExtensions.peek(this);
    }

    @Override
    default T getFirst() {
        return DequeExtensions.getFirst(this);
    }

    @Override
    default T getLast() {
        return DequeExtensions.getLast(this);
    }

    @Override
    default T element() {
        return DequeExtensions.element(this);
    }

    @Override
    default boolean offer(final T item) {
        return DequeExtensions.offer(this, item);
    }

    @Override
    default boolean add(final T item) {
        return DequeExtensions.add(this, item);
    }

    @Override
    default void push(final T item) {
        DequeExtensions.push(this, item);
    }

    @Override
    default T remove() {
        return DequeExtensions.remove(this);
    }

    @Override
    default T poll() {
        return DequeExtensions.poll(this);
    }

    @Override
    default T pollFirst() {
        return DequeExtensions.pollFirst(this);
    }

    @Override
    default T pollLast() {
        return DequeExtensions.pollLast(this);
    }

    @Override
    default T pop() {
        return DequeExtensions.pop(this);
    }

    @Override
    default boolean removeFirstOccurrence(final Object item) {
        return DequeExtensions.removeFirstOccurrence(this, item);
    }

    @Override
    default boolean removeLastOccurrence(final Object item) {
        return DequeExtensions.removeLastOccurrence(this, item);
    }

    @Override
    default Object[] toArray() {
        return CollectionExtensions.toArray(this);
    }

    @Override
    default <R> R[] toArray(final R[] array) {
        return CollectionExtensions.toArray(this, array);
    }

    @Override
    default <R> R[] toArray(final IntFunction<R[]> generator) {
        return CollectionExtensions.toArray(this, generator);
    }

    @Override
    default boolean containsAll(final Collection<?> collection) {
        return CollectionExtensions.containsAll(this, collection);
    }

    @Override
    default boolean addAll(final Collection<? extends T> collection) {
        return CollectionExtensions.addAll(this, collection);
    }

    @Override
    default boolean removeAll(final Collection<?> collection) {
        return CollectionExtensions.removeAll(this, collection);
    }

    @Override
    default boolean removeIf(final Predicate<? super T> filter) {
        return CollectionExtensions.removeIf(this, filter);
    }

    @Override
    default boolean retainAll(final Collection<?> collection) {
        return CollectionExtensions.retainAll(this, collection);
    }
}
