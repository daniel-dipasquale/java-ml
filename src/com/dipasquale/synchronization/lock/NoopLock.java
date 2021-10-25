package com.dipasquale.synchronization.lock;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoopLock implements Lock {
    private static final NoopCondition CONDITION = NoopCondition.getInstance();
    private static final NoopLock INSTANCE = new NoopLock();

    public static NoopLock getInstance() {
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
