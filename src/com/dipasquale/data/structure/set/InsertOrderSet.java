package com.dipasquale.data.structure.set;

import com.dipasquale.data.structure.queue.Node;
import com.dipasquale.data.structure.queue.NodeQueue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public interface InsertOrderSet<T> extends Set<T>, Queue<T> {
    T first();

    T last();

    @Override
    default T peek() {
        return first();
    }

    @Override
    default boolean offer(final T value) {
        return add(value);
    }

    Iterator<T> iteratorDescending();

    static <T> InsertOrderSet<T> create() {
        Map<T, Node> nodesMap = new HashMap<>();
        NodeQueue<T> nodesQueue = NodeQueue.create();

        return new InsertOrderSetDefault<>(nodesMap, nodesQueue);
    }

    static <T> InsertOrderSet<T> create(final int initialCapacity) {
        Map<T, Node> nodesMap = new HashMap<>(initialCapacity);
        NodeQueue<T> nodesQueue = NodeQueue.create();

        return new InsertOrderSetDefault<>(nodesMap, nodesQueue);
    }

    private static <T> InsertOrderSet<T> createSynchronized(final InsertOrderSet<T> set) {
        return new InsertOrderSet<>() {
            @Override
            public int size() {
                synchronized (set) {
                    return set.size();
                }
            }

            @Override
            public boolean isEmpty() {
                synchronized (set) {
                    return set.isEmpty();
                }
            }

            @Override
            public boolean contains(final Object object) {
                synchronized (set) {
                    return set.contains(object);
                }
            }

            @Override
            public T first() {
                synchronized (set) {
                    return set.first();
                }
            }

            @Override
            public T last() {
                synchronized (set) {
                    return set.last();
                }
            }

            @Override
            public T element() {
                synchronized (set) {
                    return set.element();
                }
            }

            @Override
            public T peek() {
                synchronized (set) {
                    return set.peek();
                }
            }

            @Override
            public boolean add(final T value) {
                synchronized (set) {
                    return set.add(value);
                }
            }

            @Override
            public boolean offer(final T value) {
                synchronized (set) {
                    return set.offer(value);
                }
            }

            @Override
            public boolean remove(final Object object) {
                synchronized (set) {
                    return set.remove(object);
                }
            }

            @Override
            public T remove() {
                synchronized (set) {
                    return set.remove();
                }
            }

            @Override
            public T poll() {
                synchronized (set) {
                    return set.poll();
                }
            }

            @Override
            public void clear() {
                synchronized (set) {
                    set.clear();
                }
            }

            @Override
            public Iterator<T> iterator() {
                synchronized (set) {
                    return set.iterator();
                }
            }

            @Override
            public Iterator<T> iteratorDescending() {
                synchronized (set) {
                    return set.iteratorDescending();
                }
            }

            @Override
            public Object[] toArray() {
                synchronized (set) {
                    return set.toArray();
                }
            }

            @Override
            public <R> R[] toArray(final R[] array) {
                synchronized (set) {
                    return set.toArray(array);
                }
            }

            @Override
            public boolean containsAll(final Collection<?> collection) {
                synchronized (set) {
                    return set.containsAll(collection);
                }
            }

            @Override
            public boolean addAll(final Collection<? extends T> collection) {
                synchronized (set) {
                    return set.addAll(collection);
                }
            }

            @Override
            public boolean retainAll(final Collection<?> collection) {
                synchronized (set) {
                    return set.retainAll(collection);
                }
            }

            @Override
            public boolean removeAll(final Collection<?> collection) {
                synchronized (set) {
                    return set.removeAll(collection);
                }
            }
        };
    }

    static <T> InsertOrderSet<T> createSynchronized(final int initialCapacity) {
        InsertOrderSet<T> set = create(initialCapacity);

        return createSynchronized(set);
    }

    static <T> InsertOrderSet<T> createSynchronized() {
        InsertOrderSet<T> set = create();

        return createSynchronized(set);
    }

    static <T> InsertOrderSet<T> createConcurrent() {
        Map<T, Node> nodesMap = new ConcurrentHashMap<>();
        NodeQueue<T> nodesQueue = NodeQueue.createSynchronized();

        return new InsertOrderSetDefault<>(nodesMap, nodesQueue);
    }

    static <T> InsertOrderSet<T> createConcurrent(final int initialCapacity, final float loadFactor, final int numberOfThreads) {
        Map<T, Node> nodesMap = new ConcurrentHashMap<>(initialCapacity, loadFactor, numberOfThreads);
        NodeQueue<T> nodesQueue = NodeQueue.createSynchronized();

        return new InsertOrderSetDefault<>(nodesMap, nodesQueue);
    }
}
