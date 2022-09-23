package com.dipasquale.synchronization.event.loop;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ElementProducerEventLoopHandler<T> implements EventLoopHandler {
    private final ElementProducer<T> elementProducer;
    private final ElementHandler<T> elementHandler;

    @Override
    public void handle() {
        for (ElementContainer<T> container = elementProducer.next(); container != null; container = elementProducer.next()) {
            if (!elementHandler.handle(container.getElement())) {
                return;
            }
        }
    }
}
