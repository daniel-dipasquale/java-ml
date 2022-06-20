package com.dipasquale.synchronization.event.loop;

@FunctionalInterface
interface ItemProducer<T> {
    ItemContainer<T> next();
}
