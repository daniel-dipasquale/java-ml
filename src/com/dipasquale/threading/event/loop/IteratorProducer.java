package com.dipasquale.threading.event.loop;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@FunctionalInterface
interface IteratorProducer<T> {
    Envelope<T> next();

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    @Getter
    final class Envelope<T> {
        private final T item;
    }
}
