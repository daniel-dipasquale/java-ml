package com.dipasquale.synchronization.event.loop;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class BatchingEventLoopHandler<T> implements EventLoopHandler {
    private final IteratorProducer<T> iteratorProducer;
    private final Consumer<T> itemHandler;

    @Override
    public void handle(final String name) {
        for (IteratorProducer.Container<T> container = iteratorProducer.next(); container != null; container = iteratorProducer.next()) {
            itemHandler.accept(container.getItem());
        }
    }
}
