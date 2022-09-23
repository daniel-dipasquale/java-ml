package com.dipasquale.synchronization.event.loop;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class IsolatedElementProducer<T> implements ElementProducer<T> {
    private final List<T> list; // TODO: review whether this list can be considered immutable so that the size isn't re-queried
    private int index = 0;
    private final int offset;
    private final int step;

    @Override
    public ElementContainer<T> next() {
        int i = index + offset;

        if (i >= list.size()) {
            return null;
        }

        try {
            T element = list.get(i);

            return new ElementContainer<>(element);
        } finally {
            index += step;
        }
    }
}
