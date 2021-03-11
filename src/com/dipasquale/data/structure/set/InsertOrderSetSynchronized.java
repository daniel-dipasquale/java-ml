package com.dipasquale.data.structure.set;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Iterator;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class InsertOrderSetSynchronized<T> implements InsertOrderSet<T> {
    private final InsertOrderSet<T> insertOrderSet;

    @Override
    public int size() {
        synchronized (insertOrderSet) {
            return insertOrderSet.size();
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (insertOrderSet) {
            return insertOrderSet.isEmpty();
        }
    }

    @Override
    public boolean contains(final Object object) {
        synchronized (insertOrderSet) {
            return insertOrderSet.contains(object);
        }
    }

    @Override
    public T first() {
        synchronized (insertOrderSet) {
            return insertOrderSet.first();
        }
    }

    @Override
    public T last() {
        synchronized (insertOrderSet) {
            return insertOrderSet.last();
        }
    }

    @Override
    public T element() {
        synchronized (insertOrderSet) {
            return insertOrderSet.element();
        }
    }

    @Override
    public T peek() {
        synchronized (insertOrderSet) {
            return insertOrderSet.peek();
        }
    }

    @Override
    public boolean add(final T value) {
        synchronized (insertOrderSet) {
            return insertOrderSet.add(value);
        }
    }

    @Override
    public boolean offer(final T value) {
        synchronized (insertOrderSet) {
            return insertOrderSet.offer(value);
        }
    }

    @Override
    public boolean remove(final Object object) {
        synchronized (insertOrderSet) {
            return insertOrderSet.remove(object);
        }
    }

    @Override
    public T remove() {
        synchronized (insertOrderSet) {
            return insertOrderSet.remove();
        }
    }

    @Override
    public T poll() {
        synchronized (insertOrderSet) {
            return insertOrderSet.poll();
        }
    }

    @Override
    public T pop() {
        synchronized (insertOrderSet) {
            return insertOrderSet.pop();
        }
    }

    @Override
    public void clear() {
        synchronized (insertOrderSet) {
            insertOrderSet.clear();
        }
    }

    @Override
    public Iterator<T> iterator() {
        synchronized (insertOrderSet) {
            return insertOrderSet.iterator();
        }
    }

    @Override
    public Iterator<T> iteratorDescending() {
        synchronized (insertOrderSet) {
            return insertOrderSet.iteratorDescending();
        }
    }

    @Override
    public Object[] toArray() {
        synchronized (insertOrderSet) {
            return insertOrderSet.toArray();
        }
    }

    @Override
    public <R> R[] toArray(final R[] array) {
        synchronized (insertOrderSet) {
            return insertOrderSet.toArray(array);
        }
    }

    @Override
    public boolean containsAll(final Collection<?> collection) {
        synchronized (insertOrderSet) {
            return insertOrderSet.containsAll(collection);
        }
    }

    @Override
    public boolean addAll(final Collection<? extends T> collection) {
        synchronized (insertOrderSet) {
            return insertOrderSet.addAll(collection);
        }
    }

    @Override
    public boolean retainAll(final Collection<?> collection) {
        synchronized (insertOrderSet) {
            return insertOrderSet.retainAll(collection);
        }
    }

    @Override
    public boolean removeAll(final Collection<?> collection) {
        synchronized (insertOrderSet) {
            return insertOrderSet.removeAll(collection);
        }
    }
}
