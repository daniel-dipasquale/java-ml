package com.dipasquale.threading.wait.handle;

import java.util.concurrent.TimeUnit;

public final class SlidingWaitHandle implements InternalSlidingWaitHandle {
    private static final String NAME = SlidingWaitHandle.class.getSimpleName();
    private final SingleThreadSlidingWaitHandle firstThreadWaitHandle;
    private boolean isFirstLockAcquired;
    private final ReusableCountLatch otherThreadsWaitHandle;

    public SlidingWaitHandle(final String name) {
        this.firstThreadWaitHandle = new SingleThreadSlidingWaitHandle(name);
        this.isFirstLockAcquired = false;
        this.otherThreadsWaitHandle = new ReusableCountLatch(0);
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

    private boolean await(final TimeUnitPair pair)
            throws InterruptedException {
        if (acquireFirstThreadLock()) {
            try {
                if (pair == null) {
                    firstThreadWaitHandle.await();

                    return true;
                }

                return firstThreadWaitHandle.await(pair.getTime(), pair.getUnit());
            } finally {
                releaseFirstThreadLock();
            }
        }

        if (pair == null) {
            otherThreadsWaitHandle.await();

            return true;
        }

        return otherThreadsWaitHandle.await(pair.getTime(), pair.getUnit());
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
