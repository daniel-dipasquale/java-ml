package com.dipasquale.threading.event.loop;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class EventLoopStreamHandler<T> implements EventLoopHandler {
    private final Iterator<T> iterator;
    private final EventLoopStreamAction<T> action;

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
            action.enact(item.value);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class Item<T> {
        private final T value;
    }
}
