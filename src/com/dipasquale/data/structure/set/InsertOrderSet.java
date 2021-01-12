package com.dipasquale.data.structure.set;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public interface InsertOrderSet<T> extends Set<T> {
    T first();

    T last();

    Iterator<T> iteratorDescending();

    static <T> InsertOrderSet<T> create() {
        return new InsertOrderSetDefault<>();
    }

    static <T> InsertOrderSet<T> createSynchronized() {
        InsertOrderSet<T> set = create();
        Object lock = new Object();

        return new InsertOrderSet<>() {
            @Override
            public int size() {
                synchronized (lock) {
                    return set.size();
                }
            }

            @Override
            public boolean isEmpty() {
                synchronized (lock) {
                    return set.isEmpty();
                }
            }

            @Override
            public boolean contains(final Object object) {
                synchronized (lock) {
                    return set.contains(object);
                }
            }

            @Override
            public Iterator<T> iterator() {
                synchronized (lock) {
                    return set.iterator();
                }
            }

            @Override
            public Object[] toArray() {
                synchronized (lock) {
                    return set.toArray();
                }
            }

            @Override
            public <R> R[] toArray(final R[] array) {
                synchronized (lock) {
                    return set.toArray(array);
                }
            }

            @Override
            public boolean add(final T node) {
                synchronized (lock) {
                    return set.add(node);
                }
            }

            @Override
            public boolean remove(final Object object) {
                synchronized (lock) {
                    return set.remove(object);
                }
            }

            @Override
            public boolean containsAll(final Collection<?> collection) {
                synchronized (lock) {
                    return set.containsAll(collection);
                }
            }

            @Override
            public boolean addAll(final Collection<? extends T> collection) {
                synchronized (lock) {
                    return set.addAll(collection);
                }
            }

            @Override
            public boolean retainAll(final Collection<?> collection) {
                synchronized (lock) {
                    return set.retainAll(collection);
                }
            }

            @Override
            public boolean removeAll(final Collection<?> collection) {
                synchronized (lock) {
                    return set.removeAll(collection);
                }
            }

            @Override
            public void clear() {
                synchronized (lock) {
                    set.clear();
                }
            }

            @Override
            public T first() {
                synchronized (lock) {
                    return set.first();
                }
            }

            @Override
            public T last() {
                synchronized (lock) {
                    return set.last();
                }
            }

            @Override
            public Iterator<T> iteratorDescending() {
                synchronized (lock) {
                    return set.iteratorDescending();
                }
            }
        };
    }
}
