package com.dipasquale.synchronization.event.loop;

@FunctionalInterface
interface ElementProducer<T> {
    ElementContainer<T> next();
}
