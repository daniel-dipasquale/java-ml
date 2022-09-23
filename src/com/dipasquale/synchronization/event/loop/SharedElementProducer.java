package com.dipasquale.synchronization.event.loop;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SharedElementProducer<T> implements ElementProducer<T> {
    private final Lock lock = new ReentrantLock();
    private final Iterator<T> iterator;

    @Override
    public ElementContainer<T> next() {
        lock.lock();

        try {
            if (!iterator.hasNext()) {
                return null;
            }

            return new ElementContainer<>(iterator.next());
        } finally {
            lock.unlock();
        }
    }
}
