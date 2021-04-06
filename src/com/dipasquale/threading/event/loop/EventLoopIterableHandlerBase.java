package com.dipasquale.threading.event.loop;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class EventLoopIterableHandlerBase<T> implements EventLoopHandler {
    private final Consumer<T> action;

    protected abstract Item<T> getNext();

    @Override
    public void handle(final String name) {
        for (Item<T> item = getNext(); item != null; item = getNext()) {
            action.accept(item.value);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    protected static final class Item<T> {
        private final T value;
    }
}
