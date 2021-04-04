package com.dipasquale.threading.wait.handle;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SlidingWaitHandleSingleThread implements SlidingWaitHandleInternal {
    private final String name;
    private final ReusableCountDownLatch mainWaitHandle = new ReusableCountDownLatch(0);
    private boolean isMainWaitLockAcquired = false;
    private final ReusableCountDownLatch timeoutChangeWaitHandle = new ReusableCountDownLatch(0);
    private final ReferenceBox<TimeUnitPair> timeoutChangeTimeUnitPair = new ReferenceBox<>();

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

    private void lockOrWaitTimeoutChange() {
        synchronized (timeoutChangeWaitHandle) {
            try {
                timeoutChangeWaitHandle.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                throw new RuntimeException("thread was interrupted", e);
            }

            timeoutChangeWaitHandle.countUp();
        }
    }

    private void releaseOneTimeoutChange() {
        timeoutChangeWaitHandle.countDown();
    }

    private boolean await(final TimeUnitPair mainTimeUnitPair)
            throws InterruptedException {
        boolean acquired;
        boolean changeTimeoutLocked = false;

        ensureLocked();

        try {
            if (mainTimeUnitPair == null) {
                mainWaitHandle.await();
                acquired = true;
            } else {
                acquired = mainWaitHandle.await(mainTimeUnitPair.getTime(), mainTimeUnitPair.getUnit());
            }

            if (acquired) {
                lockOrWaitTimeoutChange();
                changeTimeoutLocked = true;
            }

            for (TimeUnitPair timeUnitPair = timeoutChangeTimeUnitPair.poll(acquired); timeUnitPair != null; timeUnitPair = timeoutChangeTimeUnitPair.poll(acquired)) {
                releaseOneTimeoutChange();
                changeTimeoutLocked = false;
                ensureLocked();
                acquired = mainWaitHandle.await(timeUnitPair.getTime(), timeUnitPair.getUnit());

                if (acquired) {
                    lockOrWaitTimeoutChange();
                    changeTimeoutLocked = true;
                }
            }
        } finally {
            if (changeTimeoutLocked) {
                releaseOneTimeoutChange();
            }

            ensureUnlocked();
        }

        return acquired;
    }

    @Override
    public void await()
            throws InterruptedException {
        await(null);
    }

    @Override
    public boolean await(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return await(new TimeUnitPair(timeout, unit));
    }

    @Override
    public void changeTimeout(final long timeout, final TimeUnit unit) {
        lockOrWaitTimeoutChange();

        if (timeout > 0L) {
            timeoutChangeTimeUnitPair.set(new TimeUnitPair(timeout, unit));
        } else {
            timeoutChangeTimeUnitPair.set(null);
        }

        releaseOneTimeoutChange();
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

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class ReferenceBox<T> {
        private volatile T reference;

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

        public void set(final T value) {
            reference = value;
        }
    }
}
