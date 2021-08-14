package com.dipasquale.threading.event.loop;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ReentrantExclusiveQueue<T> implements ExclusiveQueue<T> {
    private final Lock lock;
    private final Queue<T> queue;

    ReentrantExclusiveQueue(final Queue<T> queue) {
        this(new ReentrantLock(), queue);
    }

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
