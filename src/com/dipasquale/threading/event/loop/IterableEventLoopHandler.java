package com.dipasquale.threading.event.loop;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class IterableEventLoopHandler<T> implements EventLoopHandler {
    private final IteratorProducer<T> iteratorProducer;
    private final Consumer<T> itemHandler;

    @Override
    public void handle(final String name) {
        for (IteratorProducer.Envelope<T> envelope = iteratorProducer.next(); envelope != null; envelope = iteratorProducer.next()) {
            itemHandler.accept(envelope.getItem());
        }
    }
}
