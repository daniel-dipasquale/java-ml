package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.threading.EventLoopStream;
import lombok.RequiredArgsConstructor;

import java.util.List;
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
    public <T> void forEach(final Stream<T> stream, final Consumer<T> action) {
        parallelism.forEach(stream, action);
    }

    @Override
    public void waitUntilDone()
            throws InterruptedException {
        parallelism.waitUntilDone();
    }

    @Override
    public void shutdown() {
        parallelism.shutdown();
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
        public <T> void forEach(final Stream<T> stream, final Consumer<T> action) {
            stream.forEach(action);
        }

        @Override
        public void waitUntilDone() {
        }

        @Override
        public void shutdown() {
        }
    }

    @RequiredArgsConstructor
    public static final class MultiThread implements Context.Parallelism {
        private final EventLoopStream eventLoopStream;
        private final int numberOfThreads;
        private final List<Throwable> exceptions;

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public int numberOfThreads() {
            return numberOfThreads;
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
        public <T> void forEach(final Stream<T> stream, final Consumer<T> action) {
            synchronized (eventLoopStream) {
                failIfThereAreUncaughtExceptions();
                eventLoopStream.queue(stream, action::accept);
            }
        }

        @Override
        public void waitUntilDone()
                throws InterruptedException {
            synchronized (eventLoopStream) {
                try {
                    eventLoopStream.awaitUntilDone();
                    failIfThereAreUncaughtExceptions();
                } catch (InterruptedException e) {
                    failButAddAllUncaughtExceptionsAsSuppressed(e);
                }
            }
        }

        @Override
        public void shutdown() {
            eventLoopStream.shutdown();
        }
    }
}
