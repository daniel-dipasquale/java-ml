/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.threading.event.loop;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SynchronizedIteratorProducer<T> implements IteratorProducer<T> {
    private final Iterator<T> iterator;

    @Override
    public Envelope<T> next() {
        synchronized (iterator) {
            if (!iterator.hasNext()) {
                return null;
            }

            return new Envelope<>(iterator.next());
        }
    }
}
