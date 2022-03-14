package com.dipasquale.synchronization.event.loop;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ContendedItemProducer<T> implements ItemProducer<T> {
    private final Lock lock = new ReentrantLock();
    private final Iterator<T> iterator;

    @Override
    public Container<T> next() {
        lock.lock();

        try {
            if (!iterator.hasNext()) {
                return null;
            }

            return new Container<>(iterator.next());
        } finally {
            lock.unlock();
        }
    }
}
