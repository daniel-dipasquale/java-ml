package com.dipasquale.common.concurrent;

public interface ReferenceContainerFactory<TReference, TReferenceContainer extends ReferenceContainer<TReference>> {
    TReferenceContainer create(TReference reference, RuntimeException exception);
}
