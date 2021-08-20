package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.threading.wait.handle.InteractiveWaitHandle;
import com.dipasquale.threading.wait.handle.WaitHandle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ParallelismWaitHandle implements WaitHandle {
    private final InteractiveWaitHandle invokedWaitHandle;
    private final Collection<Throwable> unhandledExceptions;

    ParallelismWaitHandle(final Collection<Throwable> unhandledExceptions) {
        this(null, unhandledExceptions);
    }

    private <T extends Exception> void fillUnhandledAsSuppressed(final T exception) {
        unhandledExceptions.forEach(exception::addSuppressed);
        unhandledExceptions.clear();
    }

    private void failIfAnyUnhandled() {
        if (unhandledExceptions.isEmpty()) {
            return;
        }

        RuntimeException exception = new IllegalStateException("exceptions were encountered in parallelism");

        fillUnhandledAsSuppressed(exception);

        throw exception;
    }

    @Override
    public void await()
            throws InterruptedException {
        try {
            if (invokedWaitHandle != null) {
                invokedWaitHandle.await();
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
            boolean acquired = invokedWaitHandle == null || invokedWaitHandle.await(timeout, unit);

            failIfAnyUnhandled();

            return acquired;
        } catch (InterruptedException e) {
            fillUnhandledAsSuppressed(e);

            throw e;
        }
    }
}
