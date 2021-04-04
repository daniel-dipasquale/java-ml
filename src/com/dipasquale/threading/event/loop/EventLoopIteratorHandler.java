package com.dipasquale.threading.event.loop;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class EventLoopIteratorHandler<T> implements EventLoopHandler {
    private final Iterator<T> iterator;
    private final Consumer<T> action;

    private Item<T> getNext() {
        synchronized (iterator) {
            if (!iterator.hasNext()) {
                return null;
            }

            return new Item<>(iterator.next());
        }
    }

    @Override
    public void handle(final String name) {
        for (Item<T> item = getNext(); item != null; item = getNext()) {
            action.accept(item.value);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class Item<T> {
        private final T value;
    }
}
