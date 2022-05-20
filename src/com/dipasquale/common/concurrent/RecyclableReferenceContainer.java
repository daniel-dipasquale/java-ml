package com.dipasquale.common.concurrent;

import com.dipasquale.common.factory.ObjectFactory;
import lombok.Generated;
import lombok.Getter;

import java.io.Serial;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Generated
@Getter
final class RecyclableReferenceContainer<T> extends ReferenceContainer<T> {
    @Serial
    private static final long serialVersionUID = -5872889004968264640L;
    private final long expirationDateTime;

    RecyclableReferenceContainer(final T reference, final RuntimeException exception, final long expirationDateTime) {
        super(reference, exception);
        this.expirationDateTime = expirationDateTime;
    }

    private static <T> ReferenceContainerFactory<T, RecyclableReferenceContainer<T>> createReferenceContainerFactory(final long expirationDateTime) {
        return (reference, exception) -> new RecyclableReferenceContainer<>(reference, exception, expirationDateTime);
    }

    public static <T> RecyclableReferenceContainer<T> initialize(final AtomicBoolean initialized, final ObjectFactory<T> referenceFactory, final AtomicReference<RecyclableReferenceContainer<T>> referenceContainer, final long expirationDateTime) {
        return initialize(initialized, referenceFactory, referenceContainer, createReferenceContainerFactory(expirationDateTime));
    }

    public static <T> RecyclableReferenceContainer<T> coalesce(final AtomicBoolean initialized, final ObjectFactory<T> referenceFactory, final AtomicReference<RecyclableReferenceContainer<T>> referenceContainer, final long expirationDateTime) {
        return coalesce(initialized, referenceFactory, referenceContainer, createReferenceContainerFactory(expirationDateTime));
    }
}
