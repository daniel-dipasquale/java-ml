package com.dipasquale.synchronization.lock;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;

abstract class AbstractRcuController implements RcuController, Serializable {
    @Serial
    private static final long serialVersionUID = 4178348959167767064L;
    private transient volatile Thread writingThread;
    private volatile Object previousWriteToken;
    private Object currentWriteToken;

    private AbstractRcuController(final Object writeToken) {
        this.writingThread = null;
        this.previousWriteToken = writeToken;
        this.currentWriteToken = writeToken;
    }

    protected AbstractRcuController() {
        this(new Object());
    }

    protected abstract Object getReadToken();

    protected abstract void clearReadToken();

    @Override
    public RcuState getState() {
        Object token = getReadToken();
        boolean isReading = token != null;
        boolean isWriting = writingThread == Thread.currentThread();

        if (isWriting) {
            return new RcuState(isReading, true, currentWriteToken);
        }

        if (isReading) {
            return new RcuState(true, false, token);
        }

        clearReadToken();

        return new RcuState(false, false, previousWriteToken);
    }

    protected abstract void setReadToken(Object token);

    @Override
    public void acquireRead() {
        setReadToken(previousWriteToken);
    }

    @Override
    public void releaseRead() {
        clearReadToken();
    }

    @Override
    public void acquireWrite() {
        writingThread = Thread.currentThread();
        previousWriteToken = currentWriteToken;
        currentWriteToken = new Object();
    }

    @Override
    public void releaseWrite() {
        previousWriteToken = currentWriteToken;
        writingThread = null;
    }

    @Serial
    private void readObject(final ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        writingThread = null;
    }
}
