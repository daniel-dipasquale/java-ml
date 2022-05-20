package com.dipasquale.common.concurrent;

import com.dipasquale.common.factory.ObjectFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Getter
public class ReferenceContainer<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 4228056105516874549L;
    private final T reference;
    private final RuntimeException exception;

    public T resolve() {
        if (exception != null) {
            throw exception;
        }

        return reference;
    }

    private static <T> boolean shouldInitialize(final boolean onlyUninitialized, final ReferenceContainer<T> referenceContainer) {
        if (!onlyUninitialized) {
            return true;
        }

        return referenceContainer.reference == null && referenceContainer.exception == null;
    }

    private static <TReference, TReferenceContainer extends ReferenceContainer<TReference>> TReferenceContainer initializeOrCoalesce(final boolean onlyUninitialized, final AtomicBoolean initialized, final boolean reset, final ObjectFactory<TReference> referenceFactory, final AtomicReference<TReferenceContainer> referenceContainer, final ReferenceContainerFactory<TReference, TReferenceContainer> referenceContainerFactory) {
        TReferenceContainer extractedReferenceContainer = referenceContainer.get();

        if (!shouldInitialize(onlyUninitialized, extractedReferenceContainer)) {
            return extractedReferenceContainer;
        }

        if (initialized.compareAndSet(false, true)) {
            try {
                TReference reference = referenceFactory.create();

                extractedReferenceContainer = referenceContainerFactory.create(reference, null);
            } catch (RuntimeException e) {
                extractedReferenceContainer = referenceContainerFactory.create(null, e);
            } catch (Throwable e) {
                RuntimeException exception = new RuntimeException(e.getMessage(), e);

                exception.setStackTrace(e.getStackTrace());

                extractedReferenceContainer = referenceContainerFactory.create(null, exception);
            }

            referenceContainer.set(extractedReferenceContainer);

            if (reset) {
                initialized.set(false);
            }
        } else {
            extractedReferenceContainer = referenceContainer.get();

            while (extractedReferenceContainer == null) {
                Thread.onSpinWait();
                extractedReferenceContainer = referenceContainer.get();
            }
        }

        return extractedReferenceContainer;
    }

    public static <TReference, TReferenceContainer extends ReferenceContainer<TReference>> TReferenceContainer initialize(final AtomicBoolean initialized, final ObjectFactory<TReference> referenceFactory, final AtomicReference<TReferenceContainer> referenceContainer, final ReferenceContainerFactory<TReference, TReferenceContainer> referenceContainerFactory) {
        return initializeOrCoalesce(false, initialized, false, referenceFactory, referenceContainer, referenceContainerFactory);
    }

    private static <T> ReferenceContainerFactory<T, ReferenceContainer<T>> createReferenceContainerFactory() {
        return ReferenceContainer::new;
    }

    public static <T> ReferenceContainer<T> initialize(final AtomicBoolean initialized, final ObjectFactory<T> referenceFactory, final AtomicReference<ReferenceContainer<T>> referenceContainer) {
        return initialize(initialized, referenceFactory, referenceContainer, createReferenceContainerFactory());
    }

    public static <TReference, TReferenceContainer extends ReferenceContainer<TReference>> TReferenceContainer coalesce(final AtomicBoolean initialized, final ObjectFactory<TReference> referenceFactory, final AtomicReference<TReferenceContainer> referenceContainer, final ReferenceContainerFactory<TReference, TReferenceContainer> referenceContainerFactory) {
        return initializeOrCoalesce(true, initialized, true, referenceFactory, referenceContainer, referenceContainerFactory);
    }

    public static <T> ReferenceContainer<T> coalesce(final AtomicBoolean initialized, final ObjectFactory<T> referenceFactory, final AtomicReference<ReferenceContainer<T>> referenceContainer) {
        return coalesce(initialized, referenceFactory, referenceContainer, createReferenceContainerFactory());
    }
}
