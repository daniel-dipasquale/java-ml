package com.dipasquale.synchronization.event.loop;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class BatchingEventLoopHandler<T> implements EventLoopHandler {
    private final ItemProducer<T> itemProducer;
    private final Consumer<T> itemHandler;

    @Override
    public void handle(final String name) {
        for (ItemProducer.Container<T> container = itemProducer.next(); container != null; container = itemProducer.next()) {
            itemHandler.accept(container.getItem());
        }
    }
}
