package com.dipasquale.synchronization.lock;

import com.dipasquale.common.factory.ObjectCloner;
import com.dipasquale.synchronization.MappedThreadStorage;

import java.io.Serial;
import java.util.List;

final class FixedRcuController extends AbstractRcuController {
    @Serial
    private static final long serialVersionUID = -6326715153900977880L;
    private final MappedThreadStorage<Object> readToken;
    private final List<Long> threadIds;

    FixedRcuController(final List<Long> threadIds) {
        this.readToken = new MappedThreadStorage<>(Object.class, threadIds);
        this.threadIds = List.copyOf(threadIds);
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
        Reference<T> fixedReference = Reference.create(this, reference);

        return new FixedRcuMonitoredReference<>(this, fixedReference, referenceCloner, threadIds);
    }
}
