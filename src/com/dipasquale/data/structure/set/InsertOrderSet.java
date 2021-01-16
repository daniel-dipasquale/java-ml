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
        Object sync = new Object();

        return new InsertOrderSet<>() {
            @Override
            public int size() {
                synchronized (sync) {
                    return set.size();
                }
            }

            @Override
            public boolean isEmpty() {
                synchronized (sync) {
                    return set.isEmpty();
                }
            }

            @Override
            public boolean contains(final Object object) {
                synchronized (sync) {
                    return set.contains(object);
                }
            }

            @Override
            public Iterator<T> iterator() {
                synchronized (sync) {
                    return set.iterator();
                }
            }

            @Override
            public Object[] toArray() {
                synchronized (sync) {
                    return set.toArray();
                }
            }

            @Override
            public <R> R[] toArray(final R[] array) {
                synchronized (sync) {
                    return set.toArray(array);
                }
            }

            @Override
            public boolean add(final T node) {
                synchronized (sync) {
                    return set.add(node);
                }
            }

            @Override
            public boolean remove(final Object object) {
                synchronized (sync) {
                    return set.remove(object);
                }
            }

            @Override
            public boolean containsAll(final Collection<?> collection) {
                synchronized (sync) {
                    return set.containsAll(collection);
                }
            }

            @Override
            public boolean addAll(final Collection<? extends T> collection) {
                synchronized (sync) {
                    return set.addAll(collection);
                }
            }

            @Override
            public boolean retainAll(final Collection<?> collection) {
                synchronized (sync) {
                    return set.retainAll(collection);
                }
            }

            @Override
            public boolean removeAll(final Collection<?> collection) {
                synchronized (sync) {
                    return set.removeAll(collection);
                }
            }

            @Override
            public void clear() {
                synchronized (sync) {
                    set.clear();
                }
            }

            @Override
            public T first() {
                synchronized (sync) {
                    return set.first();
                }
            }

            @Override
            public T last() {
                synchronized (sync) {
                    return set.last();
                }
            }

            @Override
            public Iterator<T> iteratorDescending() {
                synchronized (sync) {
                    return set.iteratorDescending();
                }
            }
        };
    }
}
