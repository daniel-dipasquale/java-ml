package com.dipasquale.data.structure.set;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.IntFunction;
import java.util.function.Predicate;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class DequeSetSynchronized<T> implements DequeSet<T> {
    @Serial
    private static final long serialVersionUID = 254104984527031305L;
    private final DequeSet<T> dequeSet;

    @Override
    public int size() {
        synchronized (dequeSet) {
            return dequeSet.size();
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (dequeSet) {
            return dequeSet.isEmpty();
        }
    }

    @Override
    public boolean contains(final Object object) {
        synchronized (dequeSet) {
            return dequeSet.contains(object);
        }
    }

    @Override
    public T getPrevious(final T value) {
        synchronized (dequeSet) {
            return dequeSet.getPrevious(value);
        }
    }

    @Override
    public T getNext(final T value) {
        synchronized (dequeSet) {
            return dequeSet.getNext(value);
        }
    }

    @Override
    public T getFirst() {
        synchronized (dequeSet) {
            return dequeSet.getFirst();
        }
    }

    @Override
    public T getLast() {
        synchronized (dequeSet) {
            return dequeSet.getLast();
        }
    }

    @Override
    public boolean addBefore(final T value, final T previousToValue) {
        synchronized (dequeSet) {
            return dequeSet.addBefore(value, previousToValue);
        }
    }

    @Override
    public boolean addAfter(final T value, final T nextToValue) {
        synchronized (dequeSet) {
            return dequeSet.addAfter(value, nextToValue);
        }
    }

    @Override
    public boolean addFirst(final T value) {
        synchronized (dequeSet) {
            return dequeSet.addFirst(value);
        }
    }

    @Override
    public boolean addLast(final T value) {
        synchronized (dequeSet) {
            return dequeSet.addLast(value);
        }
    }

    @Override
    public boolean add(final T value) {
        synchronized (dequeSet) {
            return dequeSet.add(value);
        }
    }

    @Override
    public boolean remove(final Object value) {
        synchronized (dequeSet) {
            return dequeSet.remove(value);
        }
    }

    @Override
    public T removePrevious(final T previousToValue) {
        synchronized (dequeSet) {
            return dequeSet.removePrevious(previousToValue);
        }
    }

    @Override
    public T removeNext(final T nextToValue) {
        synchronized (dequeSet) {
            return dequeSet.removeNext(nextToValue);
        }
    }

    @Override
    public T removeFirst() {
        synchronized (dequeSet) {
            return dequeSet.removeFirst();
        }
    }

    @Override
    public T removeLast() {
        synchronized (dequeSet) {
            return dequeSet.removeLast();
        }
    }

    @Override
    public void clear() {
        synchronized (dequeSet) {
            dequeSet.clear();
        }
    }

    @Override
    public Iterator<T> iterator() {
        synchronized (dequeSet) {
            return dequeSet.iterator();
        }
    }

    @Override
    public Iterator<T> descendingIterator() {
        synchronized (dequeSet) {
            return dequeSet.descendingIterator();
        }
    }

    @Override
    public Object[] toArray() {
        synchronized (dequeSet) {
            return dequeSet.toArray();
        }
    }

    @Override
    public <R> R[] toArray(final R[] array) {
        synchronized (dequeSet) {
            return dequeSet.toArray(array);
        }
    }

    @Override
    public <R> R[] toArray(final IntFunction<R[]> generator) {
        synchronized (dequeSet) {
            return dequeSet.toArray(generator);
        }
    }

    @Override
    public boolean containsAll(final Collection<?> collection) {
        synchronized (dequeSet) {
            return dequeSet.containsAll(collection);
        }
    }

    @Override
    public boolean addAll(final Collection<? extends T> collection) {
        synchronized (dequeSet) {
            return dequeSet.addAll(collection);
        }
    }

    @Override
    public boolean removeAll(final Collection<?> collection) {
        synchronized (dequeSet) {
            return dequeSet.removeAll(collection);
        }
    }

    @Override
    public boolean removeIf(final Predicate<? super T> filter) {
        synchronized (dequeSet) {
            return dequeSet.removeIf(filter);
        }
    }

    @Override
    public boolean retainAll(final Collection<?> collection) {
        synchronized (dequeSet) {
            return dequeSet.retainAll(collection);
        }
    }
}
