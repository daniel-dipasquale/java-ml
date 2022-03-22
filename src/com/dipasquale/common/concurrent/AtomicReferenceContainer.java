package com.dipasquale.common.concurrent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class AtomicReferenceContainer<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 731224291034674365L;
    private final AtomicBoolean initialized = new AtomicBoolean();
    private volatile ReferenceContainer<T> referenceContainer = null;
    private final RecyclableReferenceFactory<T> recyclableReferenceFactory;
    @Getter
    private final long expirationDateTime;

    public ReferenceContainer<T> getReferenceContainer() {
        ReferenceContainer<T> referenceContainerFixed;

        if (initialized.compareAndSet(false, true)) {
            try {
                T reference = recyclableReferenceFactory.create(expirationDateTime);

                referenceContainerFixed = new ReferenceContainer<>(reference, null, expirationDateTime);
            } catch (RuntimeException e) {
                referenceContainerFixed = new ReferenceContainer<>(null, e, expirationDateTime);
            } catch (Throwable e) {
                RuntimeException exception = new RuntimeException(e.getMessage(), e);

                referenceContainerFixed = new ReferenceContainer<>(null, exception, expirationDateTime);
            }

            referenceContainer = referenceContainerFixed;
        } else {
            referenceContainerFixed = referenceContainer;

            while (referenceContainerFixed == null) {
                Thread.onSpinWait();
                referenceContainerFixed = referenceContainer;
            }
        }

        return referenceContainerFixed;
    }
}
