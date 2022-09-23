package com.dipasquale.data.structure.iterator;

import com.dipasquale.data.structure.collection.ListSupport;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public final class ZipIterator<T> implements Iterator<List<T>> {
    private final List<Iterator<T>> iterators;

    private static <T> List<Iterator<T>> provide(final Iterable<Iterator<T>> iterators) {
        if (iterators instanceof List<?>) {
            return (List<Iterator<T>>) iterators;
        }

        return ListSupport.copyOf(iterators);
    }

    public ZipIterator(final Iterable<Iterator<T>> iterators) {
        this.iterators = provide(iterators);
    }

    @Override
    public boolean hasNext() {
        return iterators.stream()
                .anyMatch(Iterator::hasNext);
    }

    private static <T> T getNext(final Iterator<T> iterator) {
        if (!iterator.hasNext()) {
            return null;
        }

        return iterator.next();
    }

    @Override
    public List<T> next() {
        return iterators.stream()
                .map(ZipIterator::getNext)
                .collect(Collectors.toList());
    }
}
