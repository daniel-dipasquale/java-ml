package com.dipasquale.synchronization.lock;

import com.dipasquale.common.factory.ObjectCloner;
import com.dipasquale.synchronization.IsolatedThreadIndex;
import com.dipasquale.synchronization.IsolatedThreadStorage;

import java.io.Serial;
import java.io.Serializable;

final class IsolatedRcuMonitoredReference<T> implements RcuMonitoredReference<T>, Serializable {
    @Serial
    private static final long serialVersionUID = -2774931536673986660L;
    private final IsolatedRcuController controller;
    private RcuReference<T> unprotectedRcuReference;
    private RcuReference<T> writeRcuReference;
    private final ObjectCloner<T> referenceCloner;
    private final IsolatedThreadStorage<RcuReference<T>> readRcuReference;

    private static <T> RcuReference<T> createRcuReference(final IsolatedRcuController controller, final T reference) {
        Object writeToken = controller.getWriteToken();

        if (writeToken != null) {
            return new RcuReference<>(writeToken, reference);
        }

        Object readToken = controller.getReadTokenFromCurrent();

        if (readToken != null) {
            return new RcuReference<>(readToken, reference);
        }

        return new RcuReference<>(controller.getUnprotectedToken(), reference);
    }

    private static <T> Class<RcuReference<T>> getType(final RcuReference<T> rcuReference) {
        return (Class<RcuReference<T>>) rcuReference.getClass();
    }

    private IsolatedRcuMonitoredReference(final IsolatedRcuController controller, final RcuReference<T> rcuReference, final ObjectCloner<T> referenceCloner, final IsolatedThreadIndex isolatedThreadIndex) {
        this.controller = controller;
        this.unprotectedRcuReference = rcuReference;
        this.writeRcuReference = rcuReference;
        this.referenceCloner = referenceCloner;
        this.readRcuReference = new IsolatedThreadStorage<>(isolatedThreadIndex, getType(rcuReference));
    }

    IsolatedRcuMonitoredReference(final IsolatedRcuController controller, final T reference, final ObjectCloner<T> referenceCloner, final IsolatedThreadIndex isolatedThreadIndex) {
        this(controller, createRcuReference(controller, reference), referenceCloner, isolatedThreadIndex);
    }

    @Override
    public T get() {
        Object writeToken = controller.getWriteToken();

        if (writeToken != null) {
            T value = writeRcuReference.getValue();

            if (writeRcuReference.getToken() != writeToken) {
                unprotectedRcuReference = writeRcuReference;
                value = referenceCloner.clone(value);
                writeRcuReference = new RcuReference<>(writeToken, value);
            }

            return value;
        }

        int readIndex = controller.getCurrentIndex();
        Object readToken = controller.getReadToken(readIndex);

        if (readToken != null) {
            RcuReference<T> rcuReference = readRcuReference.get(readIndex);

            if (rcuReference == null || rcuReference.getToken() != readToken) {
                readRcuReference.put(readIndex, rcuReference = unprotectedRcuReference);
            }

            return rcuReference.getValue();
        }

        readRcuReference.remove(readIndex);

        return unprotectedRcuReference.getValue();
    }
}
