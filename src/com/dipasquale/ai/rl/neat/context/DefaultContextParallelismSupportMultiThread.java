package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.threading.event.loop.IterableEventLoop;
import com.dipasquale.threading.wait.handle.InteractiveWaitHandle;
import com.dipasquale.threading.wait.handle.WaitHandle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@RequiredArgsConstructor
public final class DefaultContextParallelismSupportMultiThread implements Context.ParallelismSupport {
    private final IterableEventLoop eventLoop;
    private final Collection<Throwable> unhandledExceptions = Collections.synchronizedSet(Collections.newSetFromMap(new IdentityHashMap<>()));

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int numberOfThreads() {
        return eventLoop.getConcurrencyLevel();
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
    public <T> WaitHandle forEach(final Iterator<T> iterator, final Consumer<T> itemHandler) {
        unhandledExceptions.clear();

        InteractiveWaitHandle invokedWaitHandle = eventLoop.queue(iterator, itemHandler, unhandledExceptions::add);

        return new DefaultWaitHandle(invokedWaitHandle);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class DefaultWaitHandle implements WaitHandle {
        private final InteractiveWaitHandle invokedWaitHandle;

        @Override
        public void await()
                throws InterruptedException {
            try {
                invokedWaitHandle.await();
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
                boolean acquired = invokedWaitHandle.await(timeout, unit);

                failIfAnyUnhandled();

                return acquired;
            } catch (InterruptedException e) {
                fillUnhandledAsSuppressed(e);

                throw e;
            }
        }
    }
}
