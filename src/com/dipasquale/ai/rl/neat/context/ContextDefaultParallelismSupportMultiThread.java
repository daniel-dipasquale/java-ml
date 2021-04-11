package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.threading.event.loop.EventLoopIterable;
import com.dipasquale.threading.wait.handle.WaitHandle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@RequiredArgsConstructor
public final class ContextDefaultParallelismSupportMultiThread implements Context.ParallelismSupport {
    private final EventLoopIterable eventLoop;
    private final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int numberOfThreads() {
        return eventLoop.getConcurrencyLevel();
    }

    private <T extends Exception> void addUncaughtExceptionsAsSuppressed(final T exception)
            throws T {
        exceptions.forEach(exception::addSuppressed);
        exceptions.clear();
    }

    private void failIfThereAreUncaughtExceptions() {
        if (exceptions.isEmpty()) {
            return;
        }

        RuntimeException exception = new IllegalStateException("exceptions were encountered in parallelism");

        addUncaughtExceptionsAsSuppressed(exception);

        throw exception;
    }

    @Override
    public <T> WaitHandle forEach(final Iterator<T> iterator, final Consumer<T> action) {
        failIfThereAreUncaughtExceptions();

        return new CountDownLatchWaitHandle(eventLoop.queue(iterator, action));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class CountDownLatchWaitHandle implements WaitHandle {
        private final CountDownLatch countDownLatch;

        @Override
        public void await()
                throws InterruptedException {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                addUncaughtExceptionsAsSuppressed(e);

                throw e;
            }

            failIfThereAreUncaughtExceptions();
        }

        @Override
        public boolean await(final long timeout, final TimeUnit unit)
                throws InterruptedException {
            try {
                boolean acquired = countDownLatch.await(timeout, unit);

                failIfThereAreUncaughtExceptions();

                return acquired;
            } catch (InterruptedException e) {
                addUncaughtExceptionsAsSuppressed(e);

                throw e;
            }
        }
    }
}
