package com.dipasquale.synchronization.wait.handle;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public final class StrategyWaitHandle implements WaitHandle {
    private final WaitHandle waitHandle;
    private final Collection<Throwable> unhandledExceptions;

    private <T extends Exception> void fillUnhandledAsSuppressed(final T exception) {
        unhandledExceptions.forEach(exception::addSuppressed);
    }

    private void failIfAnyUnhandled() {
        if (unhandledExceptions.isEmpty()) {
            return;
        }

        IllegalStateException exception = new IllegalStateException("exceptions were encountered in parallelism");

        fillUnhandledAsSuppressed(exception);

        throw exception;
    }

    @Override
    public void await()
            throws InterruptedException {
        try {
            if (waitHandle == null && Thread.interrupted()) {
                throw new InterruptedException("thread was interrupted");
            }

            if (waitHandle != null) {
                waitHandle.await();
            }
        } catch (InterruptedException e) {
            fillUnhandledAsSuppressed(e);

            throw e;
        }

        failIfAnyUnhandled();
    }

    @Override
    public boolean await(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        try {
            boolean acquired = waitHandle == null || waitHandle.await(timeout, unit);

            failIfAnyUnhandled();

            return acquired;
        } catch (InterruptedException e) {
            fillUnhandledAsSuppressed(e);

            throw e;
        }
    }
}
