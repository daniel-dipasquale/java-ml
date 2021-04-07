package com.dipasquale.threading.event.loop;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class EventLoopIterableHandler<T> implements EventLoopHandler {
    private final EventLoopIterableProducer<T> producer;
    private final Consumer<T> action;

    @Override
    public void handle(final String name) {
        for (EventLoopIterableProducer.Envelope<T> item = producer.next(); item != null; item = producer.next()) {
            action.accept(item.getItem());
        }
    }
}
