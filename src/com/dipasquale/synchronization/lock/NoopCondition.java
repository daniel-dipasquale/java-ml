package com.dipasquale.synchronization.lock;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoopCondition implements Condition {
    private static final NoopCondition INSTANCE = new NoopCondition();

    public static NoopCondition getInstance() {
        return INSTANCE;
    }

    @Override
    public void await()
            throws InterruptedException {
    }

    @Override
    public void awaitUninterruptibly() {
    }

    @Override
    public long awaitNanos(final long nanosTimeout)
            throws InterruptedException {
        return nanosTimeout;
    }

    @Override
    public boolean await(final long time, final TimeUnit unit)
            throws InterruptedException {
        return true;
    }

    @Override
    public boolean awaitUntil(final Date deadline)
            throws InterruptedException {
        return true;
    }

    @Override
    public void signal() {
    }

    @Override
    public void signalAll() {
    }
}
