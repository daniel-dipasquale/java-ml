package com.dipasquale.synchronization.lock;

import com.dipasquale.common.factory.ObjectCloner;

import java.io.Serial;
import java.io.Serializable;

abstract class AbstractRcuMonitoredReference<T> implements RcuMonitoredReference<T>, Serializable {
    @Serial
    private static final long serialVersionUID = 1554603056470558809L;
    private final RcuController controller;
    private volatile Reference<T> previousWriteReference;
    private Reference<T> currentWriteReference;
    private final ObjectCloner<T> referenceCloner;

    protected AbstractRcuMonitoredReference(final RcuController controller, final Reference<T> reference, final ObjectCloner<T> referenceCloner) {
        this.controller = controller;
        this.previousWriteReference = reference;
        this.currentWriteReference = reference;
        this.referenceCloner = referenceCloner;
    }

    protected AbstractRcuMonitoredReference(final RcuController controller, final T reference, final ObjectCloner<T> referenceCloner) {
        this(controller, Reference.create(controller, reference), referenceCloner);
    }

    protected abstract Reference<T> getReadReference();

    protected abstract void setReadReference(Reference<T> reference);

    protected abstract void removeReadReference();

    @Override
    public T get() {
        RcuState state = controller.getState();
        Object token = state.getToken();

        if (state.isWriting()) {
            T value = currentWriteReference.getValue();

            if (currentWriteReference.getToken() != token) {
                previousWriteReference = currentWriteReference;
                value = referenceCloner.clone(value);
                currentWriteReference = new Reference<>(token, value);
            }

            return value;
        }

        if (state.isReading()) {
            Reference<T> reference = getReadReference();

            if (reference == null || reference.getToken() != token) {
                reference = previousWriteReference;
                setReadReference(reference);
            }

            return reference.getValue();
        }

        removeReadReference();

        return previousWriteReference.getValue();
    }
}
