package com.dipasquale.synchronization.event.loop;

interface ExclusiveQueue<T> {
    void lock();

    void unlock();

    boolean isEmpty();

    T peek();

    T poll();

    void push(T item);

    void clear();
}
