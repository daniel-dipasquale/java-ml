package com.dipasquale.synchronization.lock;

import com.dipasquale.common.factory.ObjectCloner;
import com.dipasquale.synchronization.MappedThreadStorage;

import java.io.Serial;
import java.util.List;

final class FixedRcuMonitoredReference<T> extends AbstractRcuMonitoredReference<T> {
    @Serial
    private static final long serialVersionUID = -2774931536673986660L;
    private final MappedThreadStorage<Reference<T>> readReference;

    private static <T> Class<Reference<T>> getType(final Reference<T> reference) {
        return (Class<Reference<T>>) reference.getClass();
    }

    FixedRcuMonitoredReference(final RcuController controller, final Reference<T> reference, final ObjectCloner<T> referenceCloner, final List<Long> threadIds) {
        super(controller, reference, referenceCloner);
        this.readReference = new MappedThreadStorage<>(getType(reference), threadIds);
    }

    @Override
    protected Reference<T> getReadReference() {
        return readReference.getOrDefault(null);
    }

    @Override
    protected void setReadReference(final Reference<T> reference) {
        readReference.put(reference);
    }

    @Override
    protected void removeReadReference() {
        readReference.remove();
    }
}
