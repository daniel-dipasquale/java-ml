package com.dipasquale.common.concurrent;

import com.dipasquale.common.factory.ObjectFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public final class AtomicLazyReference<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -6226524128351563527L;
    private final ObjectFactory<T> referenceFactory;
    private final AtomicBoolean initialized = new AtomicBoolean();
    private volatile ReferenceEnvelope<T> referenceEnvelope = null;

    public T reference() {
        ReferenceEnvelope<T> referenceEnvelopeFixed;

        if (!initialized.compareAndSet(false, true)) {
            referenceEnvelopeFixed = referenceEnvelope;

            while (referenceEnvelopeFixed == null) {
                Thread.onSpinWait();
                referenceEnvelopeFixed = referenceEnvelope;
            }
        } else {
            referenceEnvelopeFixed = new ReferenceEnvelope<>(referenceFactory.create());
            referenceEnvelope = referenceEnvelopeFixed;
        }

        return referenceEnvelopeFixed.reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ReferenceEnvelope<T> {
        private final T reference;
    }
}
