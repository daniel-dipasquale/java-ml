package com.dipasquale.threading.event.loop;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.List;

@FunctionalInterface
interface IteratorProducer<T> {
    Envelope<T> next();

    static <T> IteratorProducer<T> createSynchronized(final Iterator<T> iterator) {
        return new SynchronizedIteratorProducer<>(iterator);
    }

    static <T> IteratorProducer<T> createConcurrent(final List<T> list) {
        return new ConcurrentIteratorProducer<>(list);
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    @Getter
    final class Envelope<T> {
        private final T item;
    }
}
