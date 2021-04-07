package com.dipasquale.threading.event.loop;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@FunctionalInterface
interface EventLoopIterableProducer<T> {
    Envelope<T> next();

    static <T> EventLoopIterableProducer<T> createSynchronized(final Iterator<T> iterator) {
        return () -> {
            synchronized (iterator) {
                if (!iterator.hasNext()) {
                    return null;
                }

                return new Envelope<>(iterator.next());
            }
        };
    }

    static <T> EventLoopIterableProducer<T> createConcurrent(final List<T> list) {
        AtomicInteger index = new AtomicInteger(0);

        return () -> {
            int indexFixed = index.getAndIncrement();

            if (indexFixed >= list.size()) {
                return null;
            }

            return new Envelope<>(list.get(indexFixed));
        };
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    final class Envelope<T> {
        private final T item;
    }
}
