/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.threading.event.loop;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class IterableEventLoopHandler<T> implements EventLoopHandler {
    private final IteratorProducer<T> iteratorProducer;
    private final Consumer<T> action;

    @Override
    public void handle(final String name) {
        for (IteratorProducer.Envelope<T> envelope = iteratorProducer.next(); envelope != null; envelope = iteratorProducer.next()) {
            action.accept(envelope.getItem());
        }
    }
}
