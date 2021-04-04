package com.dipasquale.threading.wait.handle;

import java.util.concurrent.TimeUnit;

public final class SlidingWaitHandle implements SlidingWaitHandleInternal {
    private static final String NAME = SlidingWaitHandle.class.getSimpleName();
    private final SlidingWaitHandleSingleThread firstThreadWaitHandle;
    private boolean isFirstLockAcquired;
    private final ReusableCountDownLatch otherThreadsWaitHandle;

    public SlidingWaitHandle(final String name) {
        this.firstThreadWaitHandle = new SlidingWaitHandleSingleThread(name);
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

    private boolean await(final TimeUnitPair timeUnitPair)
            throws InterruptedException {
        if (acquireFirstThreadLock()) {
            try {
                if (timeUnitPair == null) {
                    firstThreadWaitHandle.await();

                    return true;
                }

                return firstThreadWaitHandle.await(timeUnitPair.getTime(), timeUnitPair.getUnit());
            } finally {
                releaseFirstThreadLock();
            }
        }

        if (timeUnitPair == null) {
            otherThreadsWaitHandle.await();

            return true;
        }

        return otherThreadsWaitHandle.await(timeUnitPair.getTime(), timeUnitPair.getUnit());
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
        firstThreadWaitHandle.changeTimeout(timeout, unit);
    }

    @Override
    public void release() {
        firstThreadWaitHandle.release();
    }

    @Override
    public String toString() {
        return firstThreadWaitHandle.toString();
    }
}
