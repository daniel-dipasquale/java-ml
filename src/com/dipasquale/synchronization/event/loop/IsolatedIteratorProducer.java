package com.dipasquale.synchronization.event.loop;

import java.util.List;

final class IsolatedIteratorProducer<T> implements IteratorProducer<T> {
    private final List<T> list;
    private int index;
    private final int step;

    IsolatedIteratorProducer(final List<T> list, final int offset, final int step) {
        this.list = list;
        this.index = offset;
        this.step = step;
    }

    @Override
    public Envelope<T> next() {
        if (index >= list.size()) {
            return null;
        }

        T item = list.get(index);

        index += step;

        return new Envelope<>(item);
    }
}
