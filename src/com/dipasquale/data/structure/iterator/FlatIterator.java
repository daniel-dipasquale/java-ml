package com.dipasquale.data.structure.iterator;

import java.util.Iterator;
import java.util.stream.StreamSupport;

public final class FlatIterator<T> implements Iterator<T> {
    private final Iterator<T> iterator;

    public FlatIterator(final Iterable<Iterator<T>> iterators, final boolean parallel) {
        this.iterator = StreamSupport.stream(iterators.spliterator(), false)
                .map(i -> (Iterable<T>) () -> i)
                .flatMap(i -> StreamSupport.stream(i.spliterator(), parallel))
                .iterator();
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
