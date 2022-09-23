package com.dipasquale.data.structure.iterator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class LinkedIterator<T> implements Iterator<T> {
    private final ElementContainer<T> current;
    private final NextElementProducer<T> nextProducer;
    private final HasNextElementPredicate<T> hasNextPredicate;

    private LinkedIterator(final T root, final NextElementProducer<T> nextProducer, final HasNextElementPredicate<T> hasNextPredicate) {
        this.current = new ElementContainer<>(root);
        this.nextProducer = nextProducer;
        this.hasNextPredicate = hasNextPredicate;
    }

    @Override
    public boolean hasNext() {
        return hasNextPredicate.hasMore(current.element);
    }

    @Override
    public T next() {
        T currentElement = current.element;

        current.element = nextProducer.next(currentElement);

        return currentElement;
    }

    public static <T> Iterable<T> createIterable(final T root, final NextElementProducer<T> nextProducer, final HasNextElementPredicate<T> hasNextPredicate) {
        return () -> new LinkedIterator<>(root, nextProducer, hasNextPredicate);
    }

    public static <T> Iterable<T> createIterable(final T root, final NextElementProducer<T> nextProducer) {
        return createIterable(root, nextProducer, Objects::nonNull);
    }

    public static <T> Stream<T> createStream(final T root, final NextElementProducer<T> nextProducer, final HasNextElementPredicate<T> hasNextPredicate) {
        return StreamSupport.stream(createIterable(root, nextProducer, hasNextPredicate).spliterator(), false);
    }

    public static <T> Stream<T> createStream(final T root, final NextElementProducer<T> nextProducer) {
        return StreamSupport.stream(createIterable(root, nextProducer).spliterator(), false);
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ElementContainer<T> {
        private T element;
    }
}
