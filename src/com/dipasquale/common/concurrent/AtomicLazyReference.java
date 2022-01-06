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
    private final AtomicBoolean initializing = new AtomicBoolean();
    private final ObjectFactory<T> referenceFactory;
    private volatile ReferenceEnvelope<T> referenceEnvelope = null;

    public boolean initialized() {
        return referenceEnvelope != null;
    }

    public T reference() {
        ReferenceEnvelope<T> referenceEnvelopeFixed;

        if (!initializing.compareAndSet(false, true)) {
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
    private static final class ReferenceEnvelope<T> implements Serializable {
        @Serial
        private static final long serialVersionUID = 4228056105516874549L;
        private final T reference;
    }
}
