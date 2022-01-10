package com.dipasquale.data.structure.iterator;

import com.dipasquale.data.structure.collection.Lists;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public final class ZipIterator<T> implements Iterator<List<T>> {
    private final List<Iterator<T>> iterators;

    public ZipIterator(final Iterable<Iterator<T>> iterators) {
        this.iterators = getOrCreate(iterators);
    }

    private static <T> List<Iterator<T>> getOrCreate(final Iterable<Iterator<T>> iterators) {
        if (iterators instanceof List<?>) {
            return (List<Iterator<T>>) iterators;
        }

        return Lists.createCopyOf(iterators);
    }

    @Override
    public boolean hasNext() {
        return iterators.stream()
                .anyMatch(Iterator::hasNext);
    }

    @Override
    public List<T> next() {
        return iterators.stream()
                .map(iterator -> iterator.hasNext()
                        ? iterator.next()
                        : null)
                .collect(Collectors.toList());
    }
}
