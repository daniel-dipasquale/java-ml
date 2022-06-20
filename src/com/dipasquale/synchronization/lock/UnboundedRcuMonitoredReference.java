package com.dipasquale.synchronization.lock;

import com.dipasquale.common.factory.ObjectCloner;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;

final class UnboundedRcuMonitoredReference<T> extends AbstractRcuMonitoredReference<T> {
    @Serial
    private static final long serialVersionUID = -4600231322695441628L;
    private transient ThreadLocal<ReadReference<T>> readReference;

    UnboundedRcuMonitoredReference(final RcuController controller, final T reference, final ObjectCloner<T> referenceCloner) {
        super(controller, reference, referenceCloner);
        this.readReference = ThreadLocal.withInitial(ReadReference::new);
    }

    @Override
    protected Reference<T> getReadReference() {
        return readReference.get().value;
    }

    @Override
    protected void setReadReference(final Reference<T> reference) {
        readReference.get().value = reference;
    }

    @Override
    protected void removeReadReference() {
        readReference.remove();
    }

    @Serial
    private void readObject(final ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        readReference = ThreadLocal.withInitial(ReadReference::new);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ReadReference<T> implements Serializable {
        @Serial
        private static final long serialVersionUID = -3302321416004804258L;
        private Reference<T> value = null;
    }
}
