package com.dipasquale.threading.event.loop;

import java.util.Iterator;
import java.util.function.Consumer;

final class EventLoopIterableHandlerIterator<T> extends EventLoopIterableHandlerBase<T> {
    private final Iterator<T> iterator;

    EventLoopIterableHandlerIterator(final Iterator<T> iterator, final Consumer<T> action) {
        super(action);
        this.iterator = iterator;
    }

    @Override
    protected Item<T> getNext() {
        synchronized (iterator) {
            if (!iterator.hasNext()) {
                return null;
            }

            return new Item<>(iterator.next());
        }
    }
}
