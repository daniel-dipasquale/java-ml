package com.dipasquale.synchronization.lock;

import com.dipasquale.common.factory.ObjectCloner;
import com.dipasquale.synchronization.MappedThreadIndex;
import com.dipasquale.synchronization.MappedThreadStorage;

import java.io.Serial;

final class FixedRcuMonitoredReference<T> extends AbstractRcuMonitoredReference<T> {
    @Serial
    private static final long serialVersionUID = -2774931536673986660L;
    private final MappedThreadStorage<RcuReference<T>> readReference;

    private static <T> Class<RcuReference<T>> getType(final RcuReference<T> rcuReference) {
        return (Class<RcuReference<T>>) rcuReference.getClass();
    }

    FixedRcuMonitoredReference(final RcuController controller, final RcuReference<T> rcuReference, final ObjectCloner<T> referenceCloner, final MappedThreadIndex mappedThreadIndex) {
        super(controller, rcuReference, referenceCloner);
        this.readReference = new MappedThreadStorage<>(mappedThreadIndex, getType(rcuReference));
    }

    @Override
    protected RcuReference<T> getReadReference() {
        return readReference.getOrDefault(null);
    }

    @Override
    protected void setReadReference(final RcuReference<T> rcuReference) {
        readReference.put(rcuReference);
    }

    @Override
    protected void removeReadReference() {
        readReference.remove();
    }
}
