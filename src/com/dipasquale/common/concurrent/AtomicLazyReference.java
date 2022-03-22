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

    public boolean isInitialized() {
        return referenceContainer != null;
    }

    public T getReference() {
        ReferenceContainer<T> referenceContainerFixed;

        if (initialized.compareAndSet(false, true)) {
            try {
                T reference = referenceFactory.create();

                referenceContainerFixed = new ReferenceContainer<>(reference, null);
            } catch (RuntimeException e) {
                referenceContainerFixed = new ReferenceContainer<>(null, e);
            } catch (Throwable e) {
                RuntimeException exception = new RuntimeException(e.getMessage(), e);

                referenceContainerFixed = new ReferenceContainer<>(null, exception);
            }

            referenceContainer = referenceContainerFixed;
        } else {
            referenceContainerFixed = referenceContainer;

            while (referenceContainerFixed == null) {
                Thread.onSpinWait();
                referenceContainerFixed = referenceContainer;
            }
        }

        if (referenceContainerFixed.exception != null) {
            throw referenceContainerFixed.exception;
        }

        return referenceContainerFixed.reference;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ReferenceContainer<T> implements Serializable {
        @Serial
        private static final long serialVersionUID = 4228056105516874549L;
        private final T reference;
        private final RuntimeException exception;
    }
}
