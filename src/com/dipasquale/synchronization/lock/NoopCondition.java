package com.dipasquale.synchronization.lock;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoopCondition implements Condition, Serializable {
    @Serial
    private static final long serialVersionUID = 3674565360659824889L;
    private static final NoopCondition INSTANCE = new NoopCondition();

    public static NoopCondition getInstance() {
        return INSTANCE;
    }

    @Override
    public void await() {
    }

    @Override
    public void awaitUninterruptibly() {
    }

    @Override
    public long awaitNanos(final long nanosTimeout) {
        return nanosTimeout;
    }

    @Override
    public boolean await(final long time, final TimeUnit unit) {
        return true;
    }

    @Override
    public boolean awaitUntil(final Date deadline) {
        return true;
    }

    @Override
    public void signal() {
    }

    @Override
    public void signalAll() {
    }

    @Serial
    private Object readResolve() {
        return INSTANCE;
    }
}
