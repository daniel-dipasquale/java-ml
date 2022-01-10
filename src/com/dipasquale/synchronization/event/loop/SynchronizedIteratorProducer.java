package com.dipasquale.synchronization.event.loop;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SynchronizedIteratorProducer<T> implements IteratorProducer<T> {
    private final Iterator<T> iterator;

    @Override
    public Container<T> next() {
        synchronized (iterator) {
            if (!iterator.hasNext()) {
                return null;
            }

            return new Container<>(iterator.next());
        }
    }
}
