package com.dipasquale.data.structure.iterator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class LinkedIterator<T> implements Iterator<T> {
    private final ItemContainer<T> currentItemContainer;
    private final NextProducer<T> nextProducer;
    private final Predicate<T> hasNextPredicate;

    private LinkedIterator(final T root, final NextProducer<T> nextProducer, final Predicate<T> hasNextPredicate) {
        this.currentItemContainer = new ItemContainer<>(root);
        this.nextProducer = nextProducer;
        this.hasNextPredicate = hasNextPredicate;
    }

    @Override
    public boolean hasNext() {
        return hasNextPredicate.hasMore(currentItemContainer.item);
    }

    @Override
    public T next() {
        T currentItem = currentItemContainer.item;

        currentItemContainer.item = nextProducer.next(currentItem);

        return currentItem;
    }

    public static <T> Iterable<T> createIterable(final T root, final NextProducer<T> nextProducer, final Predicate<T> hasNextItemPredicate) {
        return () -> new LinkedIterator<>(root, nextProducer, hasNextItemPredicate);
    }

    public static <T> Iterable<T> createIterable(final T root, final NextProducer<T> nextProducer) {
        return createIterable(root, nextProducer, Objects::nonNull);
    }

    public static <T> Stream<T> createStream(final T root, final NextProducer<T> nextProducer, final Predicate<T> hasNextItemPredicate) {
        return StreamSupport.stream(createIterable(root, nextProducer, hasNextItemPredicate).spliterator(), false);
    }

    public static <T> Stream<T> createStream(final T root, final NextProducer<T> nextProducer) {
        return StreamSupport.stream(createIterable(root, nextProducer).spliterator(), false);
    }

    @FunctionalInterface
    public interface NextProducer<T> {
        T next(T current);
    }

    @FunctionalInterface
    public interface Predicate<T> {
        boolean hasMore(T item);
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ItemContainer<T> {
        private T item;
    }
}
