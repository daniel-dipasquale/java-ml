package com.dipasquale.synchronization.lock;

import com.dipasquale.common.factory.ObjectCloner;
import com.dipasquale.synchronization.MappedThreadIndex;
import com.dipasquale.synchronization.MappedThreadStorage;

import java.io.Serial;

final class FixedRcuController extends AbstractRcuController {
    @Serial
    private static final long serialVersionUID = -6326715153900977880L;
    private final MappedThreadStorage<Object> readToken;
    private final MappedThreadIndex mappedThreadIndex;

    FixedRcuController(final MappedThreadIndex mappedThreadIndex) {
        this.readToken = new MappedThreadStorage<>(mappedThreadIndex, Object.class);
        this.mappedThreadIndex = mappedThreadIndex;
    }

    @Override
    protected Object getReadToken() {
        return readToken.getOrDefault(null);
    }

    @Override
    protected void clearReadToken() {
        readToken.remove();
    }

    @Override
    protected void setReadToken(final Object token) {
        readToken.put(token);
    }

    @Override
    public <T> RcuMonitoredReference<T> createMonitoredReference(final T reference, final ObjectCloner<T> referenceCloner) {
        RcuReference<T> fixedRcuReference = RcuReference.create(this, reference);

        return new FixedRcuMonitoredReference<>(this, fixedRcuReference, referenceCloner, mappedThreadIndex);
    }
}
