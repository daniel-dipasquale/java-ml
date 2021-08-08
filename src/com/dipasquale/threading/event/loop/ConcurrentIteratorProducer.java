package com.dipasquale.threading.event.loop;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ConcurrentIteratorProducer<T> implements IteratorProducer<T> {
    private final List<T> list;
    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public Envelope<T> next() {
        int indexFixed = index.getAndIncrement();

        if (indexFixed >= list.size()) {
            return null;
        }

        return new Envelope<>(list.get(indexFixed));
    }
}
