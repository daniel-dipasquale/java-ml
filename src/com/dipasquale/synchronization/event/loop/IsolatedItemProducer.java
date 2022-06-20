package com.dipasquale.synchronization.event.loop;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class IsolatedItemProducer<T> implements ItemProducer<T> {
    private final List<T> list; // TODO: review whether this list can be considered immutable so that the size isn't re-queried
    private int index = 0;
    private final int offset;
    private final int step;

    @Override
    public ItemContainer<T> next() {
        int i = index + offset;

        if (i >= list.size()) {
            return null;
        }

        try {
            T item = list.get(i);

            return new ItemContainer<>(item);
        } finally {
            index += step;
        }
    }
}
