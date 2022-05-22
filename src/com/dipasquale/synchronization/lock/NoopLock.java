package com.dipasquale.synchronization.lock;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoopLock implements Lock, Serializable {
    @Serial
    private static final long serialVersionUID = -4187279434550271229L;
    private static final NoopCondition CONDITION = NoopCondition.getInstance();
    private static final NoopLock INSTANCE = new NoopLock();

    public static NoopLock getInstance() {
        return INSTANCE;
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }

    @Override
    public void lock() {
    }

    @Override
    public void lockInterruptibly() {
    }

    @Override
    public boolean tryLock() {
        return true;
    }

    @Override
    public boolean tryLock(final long time, final TimeUnit unit) {
        return true;
    }

    @Override
    public void unlock() {
    }

    @Override
    public Condition newCondition() {
        return CONDITION;
    }
}
