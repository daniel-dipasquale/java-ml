package com.dipasquale.threading.event.loop;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Queue;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class UnlockedExclusiveQueue<T> implements ExclusiveQueue<T> {
    private final Queue<T> queue;

    @Override
    public void lock() {
    }

    @Override
    public void unlock() {
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
