package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.threading.event.loop.EventLoopStream;
import com.dipasquale.threading.wait.handle.WaitHandle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

@RequiredArgsConstructor
public final class ContextDefaultParallelism implements Context.Parallelism {
    private final Context.Parallelism parallelism;

    @Override
    public boolean isEnabled() {
        return parallelism.isEnabled();
    }

    @Override
    public int numberOfThreads() {
        return parallelism.numberOfThreads();
    }

    @Override
    public <T> WaitHandle forEach(final Stream<T> stream, final Consumer<T> action) {
        return parallelism.forEach(stream, action);
    }

    public static final class SingleThread implements Context.Parallelism {
        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public int numberOfThreads() {
            return 1;
        }

        @Override
        public <T> WaitHandle forEach(final Stream<T> stream, final Consumer<T> action) {
            stream.forEach(action);

            return null;
        }
    }

    @RequiredArgsConstructor
    public static final class MultiThread implements Context.Parallelism {
        private final EventLoopStream eventLoopStream;
        private final List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public int numberOfThreads() {
            return eventLoopStream.getConcurrencyLevel();
        }

        private <T extends Exception> void failButAddAllUncaughtExceptionsAsSuppressed(final T exception)
                throws T {
            exceptions.forEach(exception::addSuppressed);
            exceptions.clear();

            throw exception;
        }

        private void failIfThereAreUncaughtExceptions() {
            if (exceptions.isEmpty()) {
                return;
            }

            failButAddAllUncaughtExceptionsAsSuppressed(new IllegalStateException("exceptions were encountered in parallelism"));
        }

        @Override
        public <T> WaitHandle forEach(final Stream<T> stream, final Consumer<T> action) {
            synchronized (eventLoopStream) {
                failIfThereAreUncaughtExceptions();

                return new CountDownLatchWaitHandle(eventLoopStream.queue(stream, action::accept));
            }
        }

        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        private final class CountDownLatchWaitHandle implements WaitHandle {
            private final CountDownLatch countDownLatch;

            @Override
            public void await()
                    throws InterruptedException {
                failIfThereAreUncaughtExceptions();

                countDownLatch.await();
            }

            @Override
            public boolean await(final long timeout, final TimeUnit unit)
                    throws InterruptedException {
                failIfThereAreUncaughtExceptions();

                return countDownLatch.await(timeout, unit);
            }
        }
    }
}
