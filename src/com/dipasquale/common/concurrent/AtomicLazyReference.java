package com.dipasquale.common.concurrent;

import com.dipasquale.common.ObjectFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public final class AtomicLazyReference<T> {
    private final ObjectFactory<T> referenceFactory;
    private final AtomicBoolean initializedCas = new AtomicBoolean();
    private final AtomicReference<Envelope> envelopeCas = new AtomicReference<>();

    public T reference() {
        if (initializedCas.compareAndSet(false, true)) {
            envelopeCas.set(new Envelope(referenceFactory.create()));
        }

        Envelope envelope = envelopeCas.get();

        while (envelope == null) {
            envelope = envelopeCas.get();
        }

        return envelope.reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class Envelope {
        private final T reference;
    }
}
