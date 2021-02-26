package com.dipasquale.threading;

interface ExclusiveQueue<T> {
    void lock();

    void unlock();

    boolean isEmpty();

    T peek();

    T poll();

    void push(T item);

    void clear();
}
