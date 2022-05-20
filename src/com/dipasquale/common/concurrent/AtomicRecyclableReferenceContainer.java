package com.dipasquale.common.concurrent;

import com.dipasquale.common.factory.ObjectFactory;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

final class AtomicRecyclableReferenceContainer<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 731224291034674365L;
    private final AtomicBoolean initialized;
    private final ObjectFactory<T> referenceFactory;
    private final AtomicReference<RecyclableReferenceContainer<T>> referenceContainer;
    @Getter
    private final long expirationDateTime;

    AtomicRecyclableReferenceContainer(final RecyclableReferenceFactory<T> recyclableReferenceFactory, final long expirationDateTime) {
        this.initialized = new AtomicBoolean();
        this.referenceFactory = (ObjectFactory<T> & Serializable) () -> recyclableReferenceFactory.create(expirationDateTime);
        this.referenceContainer = new AtomicReference<>();
        this.expirationDateTime = expirationDateTime;
    }

    public RecyclableReferenceContainer<T> get() {
        return RecyclableReferenceContainer.initialize(initialized, referenceFactory, referenceContainer, expirationDateTime);
    }
}
