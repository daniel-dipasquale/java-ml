package com.dipasquale.threading;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

public final class SlidingWaitHandle implements SlidingWaitHandleInternal {
    private static final String NAME = SlidingWaitHandle.class.getSimpleName();
    private final SingleThreadSlidingWaitHandle firstThreadWaitHandle;
    private final ReusableCountDownLatch otherThreadsWaitHandle;
    private boolean isFirstLockAcquired;

    public SlidingWaitHandle(final String name) {
        this.firstThreadWaitHandle = new SingleThreadSlidingWaitHandle(name);
        this.isFirstLockAcquired = false;
        this.otherThreadsWaitHandle = new ReusableCountDownLatch(0);
    }

    public SlidingWaitHandle() {
        this(NAME);
    }

    private boolean acquireFirstThreadLock() {
        synchronized (otherThreadsWaitHandle) {
            if (!isFirstLockAcquired) {
                isFirstLockAcquired = true;
                otherThreadsWaitHandle.countUp();

                return true;
            }

            return false;
        }
    }

    private void releaseFirstThreadLock() {
        synchronized (otherThreadsWaitHandle) {
            otherThreadsWaitHandle.countDown();
            isFirstLockAcquired = false;
        }
    }

    private boolean await(final AwaitHandler firstThreadWaitHandler, final AwaitHandler otherThreadsWaitHandler)
            throws InterruptedException {
        if (acquireFirstThreadLock()) {
            try {
                return firstThreadWaitHandler.await();
            } finally {
                releaseFirstThreadLock();
            }
        }

        return otherThreadsWaitHandler.await();
    }

    @Override
    public void await()
            throws InterruptedException {
        await(AwaitHandler.proxy(firstThreadWaitHandle), AwaitHandler.proxy(otherThreadsWaitHandle));
    }

    @Override
    public boolean await(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return await(AwaitHandler.proxy(firstThreadWaitHandle, timeout, unit), AwaitHandler.proxy(otherThreadsWaitHandle, timeout, unit));
    }

    @Override
    public void changeAwait(final long timeout, final TimeUnit unit) {
        firstThreadWaitHandle.changeAwait(timeout, unit);
    }

    @Override
    public void release() {
        firstThreadWaitHandle.release();
    }

    @Override
    public String toString() {
        return firstThreadWaitHandle.toString();
    }

    @FunctionalInterface
    private interface AwaitHandler {
        static AwaitHandler proxy(final SingleThreadSlidingWaitHandle awaitHandle) {
            return () -> {
                awaitHandle.await();

                return true;
            };
        }

        static AwaitHandler proxy(final SingleThreadSlidingWaitHandle awaitHandle, final long timeout, final TimeUnit unit) {
            return () -> awaitHandle.await(timeout, unit);
        }

        static AwaitHandler proxy(final ReusableCountDownLatch awaitHandle) {
            return () -> {
                awaitHandle.await();

                return true;
            };
        }

        static AwaitHandler proxy(final ReusableCountDownLatch awaitHandle, final long timeout, final TimeUnit unit) {
            return () -> awaitHandle.await(timeout, unit);
        }

        boolean await() throws InterruptedException;
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class TimeUnitPair {
        private final long time;
        private final TimeUnit unit;
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class ReferenceBox<T> {
        private volatile T reference;

        public T get() {
            return reference;
        }

        public void set(final T value) {
            reference = value;
        }

        public T poll(final boolean acquired) {
            if (acquired) {
                try {
                    return reference;
                } finally {
                    reference = null;
                }
            }

            return reference = null;
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class SingleThreadSlidingWaitHandle implements SlidingWaitHandleInternal {
        private final String name;
        private final ReusableCountDownLatch mainWaitHandle = new ReusableCountDownLatch(0);
        private final ReferenceBox<TimeUnitPair> swapWaitParams = new ReferenceBox<>();
        private final ReusableCountDownLatch swapWaitParamsWaitHandle = new ReusableCountDownLatch(0);
        private boolean isMainWaitLockAcquired = false;

        private void ensureLocked() {
            synchronized (mainWaitHandle) {
                if (!isMainWaitLockAcquired) {
                    isMainWaitLockAcquired = true;
                    mainWaitHandle.countUp();
                }
            }
        }

        private void ensureUnlocked() {
            synchronized (mainWaitHandle) {
                if (isMainWaitLockAcquired) {
                    isMainWaitLockAcquired = false;
                    mainWaitHandle.countDown();
                }
            }
        }

        private boolean awaitSwapWaitParams() {
            synchronized (swapWaitParamsWaitHandle) {
                try {
                    swapWaitParamsWaitHandle.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                    throw new RuntimeException("thread was interrupted", e);
                }

                swapWaitParamsWaitHandle.countUp();
            }

            return true;
        }

        private boolean releaseSwapWaitParams() {
            swapWaitParamsWaitHandle.countDown();

            return false;
        }

        private boolean await(final AwaitHandler mainAwaitHandler)
                throws InterruptedException {
            boolean acquired;
            boolean waitingSwapWaitParams = false;

            ensureLocked();

            try {
                acquired = mainAwaitHandler.await();
                waitingSwapWaitParams = awaitSwapWaitParams();

                for (TimeUnitPair params = swapWaitParams.poll(acquired); params != null; params = swapWaitParams.poll(acquired)) {
                    waitingSwapWaitParams = releaseSwapWaitParams();
                    ensureLocked();
                    acquired = mainWaitHandle.await(params.time, params.unit);
                    waitingSwapWaitParams = awaitSwapWaitParams();
                }
            } finally {
                if (waitingSwapWaitParams) {
                    releaseSwapWaitParams();
                }

                ensureUnlocked();
            }

            return acquired;
        }

        @Override
        public void await()
                throws InterruptedException {
            await(AwaitHandler.proxy(mainWaitHandle));
        }

        @Override
        public boolean await(final long timeout, final TimeUnit unit)
                throws InterruptedException {
            return await(AwaitHandler.proxy(mainWaitHandle, timeout, unit));
        }

        @Override
        public void changeAwait(final long timeout, final TimeUnit unit) {
            awaitSwapWaitParams();

            if (timeout > 0L) {
                swapWaitParams.set(new TimeUnitPair(timeout, unit));
            } else {
                swapWaitParams.set(null);
            }

            releaseSwapWaitParams();
            ensureUnlocked();
        }

        @Override
        public void release() {
            ensureUnlocked();
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
