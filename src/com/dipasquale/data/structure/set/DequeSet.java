package com.dipasquale.data.structure.set;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

public interface DequeSet<T> extends Set<T>, Serializable {
    T getPrevious(T value);

    T getNext(T value);

    T getFirst();

    T getLast();

    boolean addBefore(T value, T previousToValue);

    boolean addAfter(T value, T nextToValue);

    boolean addFirst(T value);

    boolean addLast(T value);

    T removePrevious(T previousToValue);

    T removeNext(T nextToValue);

    T removeFirst();

    T removeLast();

    Iterator<T> descendingIterator();

    static <T> DequeSet<T> createSynchronized(final DequeSet<T> dequeSet) {
        return new SynchronizedDequeSet<>(dequeSet);
    }
}
