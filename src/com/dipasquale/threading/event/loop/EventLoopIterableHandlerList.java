package com.dipasquale.threading.event.loop;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

final class EventLoopIterableHandlerList<T> extends EventLoopIterableHandlerBase<T> {
    private final List<T> list;
    private final AtomicInteger index;

    EventLoopIterableHandlerList(final List<T> list, final Consumer<T> action) {
        super(action);
        this.list = list;
        this.index = new AtomicInteger(0);
    }

    @Override
    protected Item<T> getNext() {
        int indexFixed = index.getAndIncrement();

        if (indexFixed >= list.size()) {
            return null;
        }

        return new Item<>(list.get(indexFixed));
    }
}
