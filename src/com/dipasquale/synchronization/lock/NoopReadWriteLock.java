package com.dipasquale.synchronization.lock;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoopReadWriteLock implements ReadWriteLock, Serializable {
    @Serial
    private static final long serialVersionUID = -6752472207290412858L;
    private static final Lock LOCK = NoopLock.getInstance();
    private static final NoopReadWriteLock INSTANCE = new NoopReadWriteLock();

    public static NoopReadWriteLock getInstance() {
        return INSTANCE;
    }

    @Override
    public Lock readLock() {
        return LOCK;
    }

    @Override
    public Lock writeLock() {
        return LOCK;
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }
}
