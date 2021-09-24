package com.dipasquale.synchronization.event.loop;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class IsolatedIteratorProducer<T> implements IteratorProducer<T> {
    private final List<T> list;
    private int index = 0;
    private final int offset;
    private final int step;

    @Override
    public Envelope<T> next() {
        int i = index + offset;

        if (i >= list.size()) {
            return null;
        }

        try {
            T item = list.get(i);

            return new Envelope<>(item);
        } finally {
            index += step;
        }
    }
}
