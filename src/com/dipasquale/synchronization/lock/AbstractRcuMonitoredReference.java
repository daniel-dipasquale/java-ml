package com.dipasquale.synchronization.lock;

import com.dipasquale.common.factory.ObjectCloner;

import java.io.Serial;
import java.io.Serializable;

abstract class AbstractRcuMonitoredReference<T> implements RcuMonitoredReference<T>, Serializable {
    @Serial
    private static final long serialVersionUID = 1554603056470558809L;
    private final RcuController controller;
    private volatile RcuReference<T> previousWriteRcuReference;
    private RcuReference<T> currentWriteRcuReference;
    private final ObjectCloner<T> referenceCloner;

    protected AbstractRcuMonitoredReference(final RcuController controller, final RcuReference<T> rcuReference, final ObjectCloner<T> referenceCloner) {
        this.controller = controller;
        this.previousWriteRcuReference = rcuReference;
        this.currentWriteRcuReference = rcuReference;
        this.referenceCloner = referenceCloner;
    }

    protected AbstractRcuMonitoredReference(final RcuController controller, final T reference, final ObjectCloner<T> referenceCloner) {
        this(controller, RcuReference.create(controller, reference), referenceCloner);
    }

    protected abstract RcuReference<T> getReadReference();

    protected abstract void setReadReference(RcuReference<T> rcuReference);

    protected abstract void removeReadReference();

    @Override
    public T get() {
        RcuState state = controller.getState();
        Object token = state.getToken();

        if (state.isWriting()) {
            T value = currentWriteRcuReference.getValue();

            if (currentWriteRcuReference.getToken() != token) {
                previousWriteRcuReference = currentWriteRcuReference;
                value = referenceCloner.clone(value);
                currentWriteRcuReference = new RcuReference<>(token, value);
            }

            return value;
        }

        if (state.isReading()) {
            RcuReference<T> rcuReference = getReadReference();

            if (rcuReference == null || rcuReference.getToken() != token) {
                rcuReference = previousWriteRcuReference;
                setReadReference(rcuReference);
            }

            return rcuReference.getValue();
        }

        removeReadReference();

        return previousWriteRcuReference.getValue();
    }
}
