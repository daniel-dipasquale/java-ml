package com.dipasquale.synchronization.event.loop;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@FunctionalInterface
interface IteratorProducer<T> {
    Container<T> next();

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    @Getter
    final class Container<T> {
        private final T item;
    }
}
