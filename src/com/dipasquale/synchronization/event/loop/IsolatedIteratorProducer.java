package com.dipasquale.synchronization.event.loop;

import java.util.List;

final class IsolatedIteratorProducer<T> implements IteratorProducer<T> {
    private final List<T> list;
    private int index;
    private final int size;

    IsolatedIteratorProducer(final List<T> list, final int offset, final int size) {
        this.list = list;
        this.index = offset;
        this.size = size;
    }

    @Override
    public Envelope<T> next() {
        if (index >= list.size()) {
            return null;
        }

        T item = list.get(index);

        index += size;

        return new Envelope<>(item);
    }
}
