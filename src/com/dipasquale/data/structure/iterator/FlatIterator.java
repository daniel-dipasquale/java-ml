package com.dipasquale.data.structure.iterator;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class FlatIterator<T> implements Iterator<T> {
    private final Iterator<T> iterator;

    public static <T> FlatIterator<T> fromIterables(final Iterable<Iterable<T>> iterables) {
        Iterator<T> iterator = StreamSupport.stream(iterables.spliterator(), false)
                .flatMap(i -> StreamSupport.stream(i.spliterator(), false))
                .iterator();

        return new FlatIterator<>(iterator);
    }

    public static <T> FlatIterator<T> fromIterators(final Iterable<Iterator<T>> iterators) {
        Iterable<Iterable<T>> iterables = StreamSupport.stream(iterators.spliterator(), false)
                .map(i -> (Iterable<T>) () -> i)
                ::iterator;

        return fromIterables(iterables);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        return iterator.next();
    }
}
