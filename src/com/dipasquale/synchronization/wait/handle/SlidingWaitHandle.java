package com.dipasquale.synchronization.wait.handle;

import java.util.concurrent.TimeUnit;

public final class SlidingWaitHandle implements InternalSlidingWaitHandle {
    private static final String NAME = SlidingWaitHandle.class.getSimpleName();
    private final SingleThreadSlidingWaitHandle firstThreadWaitHandle;
    private final ReusableCountingWaitHandle otherThreadsWaitHandle;
    private boolean isOtherThreadsWaitHandleOn;

    public SlidingWaitHandle(final String name) {
        this.firstThreadWaitHandle = new SingleThreadSlidingWaitHandle(name);
        this.otherThreadsWaitHandle = new ReusableCountingWaitHandle(0);
        this.isOtherThreadsWaitHandleOn = false;
    }

    public SlidingWaitHandle() {
        this(NAME);
    }

    private boolean ensureOtherThreadsWaitHandleIsOn() {
        synchronized (otherThreadsWaitHandle) {
            if (!isOtherThreadsWaitHandleOn) {
                isOtherThreadsWaitHandleOn = true;
                otherThreadsWaitHandle.countUp();

                return true;
            }

            return false;
        }
    }

    private void ensureOtherThreadsWaitHandleIsOff() {
        synchronized (otherThreadsWaitHandle) {
            isOtherThreadsWaitHandleOn = false;
            otherThreadsWaitHandle.countDown();
        }
    }

    private boolean await(final TimeUnitPair value)
            throws InterruptedException {
        if (ensureOtherThreadsWaitHandleIsOn()) {
            try {
                if (value == null) {
                    firstThreadWaitHandle.await();

                    return true;
                }

                return firstThreadWaitHandle.await(value.getTime(), value.getUnit());
            } finally {
                ensureOtherThreadsWaitHandleIsOff();
            }
        }

        if (value == null) {
            otherThreadsWaitHandle.await();

            return true;
        }

        return otherThreadsWaitHandle.await(value.getTime(), value.getUnit());
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
