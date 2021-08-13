package com.dipasquale.threading.wait.handle;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SingleThreadSlidingWaitHandle implements InternalSlidingWaitHandle {
    private final String name;
    private final ReusableCountLatch singleThreadWaitHandle = new ReusableCountLatch(0);
    private boolean isSingleThreadWaitHandleOn = false;
    private final ReusableCountLatch awaitOverrideWaitHandle = new ReusableCountLatch(0);
    private final AtomicReference<TimeUnitPair> latestAwaitOverride = new AtomicReference<>();

    private void ensureSingleThreadWaitHandleIsOn() {
        synchronized (singleThreadWaitHandle) {
            if (!isSingleThreadWaitHandleOn) {
                isSingleThreadWaitHandleOn = true;
                singleThreadWaitHandle.countUp();
            }
        }
    }

    private void ensureSingleThreadWaitHandleIsOff() {
        synchronized (singleThreadWaitHandle) {
            if (isSingleThreadWaitHandleOn) {
                isSingleThreadWaitHandleOn = false;
                singleThreadWaitHandle.countDown();
            }
        }
    }

    private boolean lockOrWaitForAwaitOverride()
            throws InterruptedException {
        synchronized (awaitOverrideWaitHandle) {
            awaitOverrideWaitHandle.await();
            awaitOverrideWaitHandle.countUp();

            return isSingleThreadWaitHandleOn;
        }
    }

    private void releaseOneAwaitOverride() {
        awaitOverrideWaitHandle.countDown();
    }

    private boolean await(final TimeUnitPair value)
            throws InterruptedException {
        boolean acquired;
        boolean awaitOverrideIsOn = false;

        ensureSingleThreadWaitHandleIsOn();

        try {
            if (value == null) {
                singleThreadWaitHandle.await();
                acquired = true;
            } else {
                acquired = singleThreadWaitHandle.await(value.getTime(), value.getUnit());
            }

            for (boolean keepTrying = acquired; keepTrying; ) {
                lockOrWaitForAwaitOverride();
                awaitOverrideIsOn = true;

                TimeUnitPair latestValue = latestAwaitOverride.getAndSet(null);

                if (latestValue != null) {
                    releaseOneAwaitOverride();
                    awaitOverrideIsOn = false;
                    ensureSingleThreadWaitHandleIsOn();
                    acquired = singleThreadWaitHandle.await(latestValue.getTime(), latestValue.getUnit());
                    keepTrying = acquired;
                } else {
                    keepTrying = false;
                }
            }
        } finally {
            if (awaitOverrideIsOn) {
                releaseOneAwaitOverride();
            }

            ensureSingleThreadWaitHandleIsOff();
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
        boolean locked;

        try {
            locked = lockOrWaitForAwaitOverride();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new RuntimeException("thread was interrupted", e);
        }

        if (locked && timeout > 0L) {
            latestAwaitOverride.set(new TimeUnitPair(timeout, unit));
        } else {
            latestAwaitOverride.set(null);
        }

        releaseOneAwaitOverride();
        ensureSingleThreadWaitHandleIsOff();
    }

    @Override
    public void release() {
        ensureSingleThreadWaitHandleIsOff();
    }

    @Override
    public String toString() {
        return name;
    }
}
