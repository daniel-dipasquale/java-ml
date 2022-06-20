package com.dipasquale.synchronization.event.loop;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ItemProducerEventLoopHandler<T> implements EventLoopHandler {
    private final ItemProducer<T> itemProducer;
    private final ItemHandler<T> itemHandler;

    @Override
    public void handle() {
        for (ItemContainer<T> container = itemProducer.next(); container != null; container = itemProducer.next()) {
            if (!itemHandler.handle(container.getItem())) {
                return;
            }
        }
    }
}
