package com.dipasquale.synchronization.lock;

import com.dipasquale.common.factory.ObjectCloner;

public interface RcuController {
    void acquireRead();

    void releaseRead();

    void acquireWrite();

    void releaseWrite();

    <T> RcuMonitoredReference<T> createMonitoredReference(T reference, ObjectCloner<T> referenceCloner);
}
