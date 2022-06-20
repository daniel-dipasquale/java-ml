package com.dipasquale.synchronization.lock;

import com.dipasquale.common.factory.ObjectCloner;

interface RcuController {
    RcuState getState();

    void acquireRead();

    void releaseRead();

    void acquireWrite();

    void releaseWrite();

    <T> RcuMonitoredReference<T> createMonitoredReference(T reference, ObjectCloner<T> referenceCloner);
}
