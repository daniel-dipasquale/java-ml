package com.dipasquale.synchronization.lock;

import com.dipasquale.common.factory.ObjectCloner;
import com.dipasquale.synchronization.IsolatedThreadIndex;
import com.dipasquale.synchronization.IsolatedThreadStorage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;

final class IsolatedRcuController implements RcuController, Serializable {
    @Serial
    private static final long serialVersionUID = -6326715153900977880L;
    private static final long EMPTY_THREAD_ID = -1L;
    private transient long writingThreadId;
    private Object writeToken;
    private Object unprotectedToken;
    private final IsolatedThreadIndex isolatedThreadIndex;
    private final IsolatedThreadStorage<Object> readToken;

    private IsolatedRcuController(final Object writeToken, final IsolatedThreadIndex isolatedThreadIndex) {
        this.writingThreadId = EMPTY_THREAD_ID;
        this.writeToken = writeToken;
        this.unprotectedToken = writeToken;
        this.isolatedThreadIndex = isolatedThreadIndex;
        this.readToken = new IsolatedThreadStorage<>(isolatedThreadIndex);
    }

    IsolatedRcuController(final IsolatedThreadIndex isolatedThreadIndex) {
        this(new Object(), isolatedThreadIndex);
    }

    public Object getWriteToken() {
        if (writingThreadId != Thread.currentThread().getId()) {
            return null;
        }

        return writeToken;
    }

    public Object getUnprotectedToken() {
        return unprotectedToken;
    }

    public int getCurrentIndex() {
        return isolatedThreadIndex.getCurrentIndex();
    }

    public Object getReadToken(final int index) {
        return readToken.get(index);
    }

    public Object getReadTokenFromCurrent() {
        return readToken.fetch();
    }

    @Override
    public void acquireRead() {
        readToken.attach(unprotectedToken);
    }

    @Override
    public void releaseRead() {
        readToken.detach();
    }

    @Override
    public void acquireWrite() {
        writingThreadId = Thread.currentThread().getId();
        unprotectedToken = writeToken;
        writeToken = new Object();
    }

    @Override
    public void releaseWrite() {
        unprotectedToken = writeToken;
        writingThreadId = EMPTY_THREAD_ID;
    }

    @Serial
    private void readObject(final ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        writingThreadId = EMPTY_THREAD_ID;
    }

    @Override
    public <T> RcuMonitoredReference<T> createMonitoredReference(final T reference, final ObjectCloner<T> referenceCloner) {
        return new IsolatedRcuMonitoredReference<>(this, reference, referenceCloner, isolatedThreadIndex);
    }
}
