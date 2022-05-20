package com.dipasquale.common.concurrent;

import com.dipasquale.common.factory.ObjectFactory;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public final class AtomicLazyReference<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -6226524128351563527L;
    private final AtomicBoolean initialized = new AtomicBoolean();
    private final ObjectFactory<T> referenceFactory;
    private final AtomicReference<ReferenceContainer<T>> referenceContainer = new AtomicReference<>();

    public boolean isInitialized() {
        return referenceContainer.get() != null;
    }

    public T getReference() {
        return ReferenceContainer.initialize(initialized, referenceFactory, referenceContainer).resolve();
    }
}
