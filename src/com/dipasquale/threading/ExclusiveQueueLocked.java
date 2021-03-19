package com.dipasquale.threading;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Queue;
import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ExclusiveQueueLocked<T> implements ExclusiveQueue<T> {
    private final Lock lock;
    private final Queue<T> queue;

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public T peek() {
        return queue.peek();
    }

    @Override
    public T poll() {
        return queue.poll();
    }

    @Override
    public void push(final T item) {
        queue.add(item);
    }

    @Override
    public void clear() {
        queue.clear();
    }
}