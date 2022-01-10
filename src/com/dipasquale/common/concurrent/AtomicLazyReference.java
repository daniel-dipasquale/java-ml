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
    private final AtomicBoolean initialized = new AtomicBoolean();
    private final ObjectFactory<T> referenceFactory;
    private volatile ReferenceContainer<T> referenceContainer = null;

    public boolean initialized() {
        return referenceContainer != null;
    }

    public T reference() {
        ReferenceContainer<T> referenceContainerFixed;

        if (!initialized.compareAndSet(false, true)) {
            referenceContainerFixed = referenceContainer;

            while (referenceContainerFixed == null) {
                Thread.onSpinWait();
                referenceContainerFixed = referenceContainer;
            }
        } else {
            referenceContainerFixed = new ReferenceContainer<>(referenceFactory.create());
            referenceContainer = referenceContainerFixed;
        }

        return referenceContainerFixed.reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ReferenceContainer<T> implements Serializable {
        @Serial
        private static final long serialVersionUID = 4228056105516874549L;
        private final T reference;
    }
}
