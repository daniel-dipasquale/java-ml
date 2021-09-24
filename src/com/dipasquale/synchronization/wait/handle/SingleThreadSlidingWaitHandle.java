package com.dipasquale.synchronization.wait.handle;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class SingleThreadSlidingWaitHandle implements InternalSlidingWaitHandle {
    private final TogglingWaitHandle singleThreadWaitHandle = new TogglingWaitHandle();
    private final ReusableCountingWaitHandle awaitOverrideWaitHandle = new ReusableCountingWaitHandle(0);
    private final AtomicReference<TimeUnitPair> latestAwaitOverride = new AtomicReference<>();

    private void ensureSingleThreadWaitHandleIsOn() {
        synchronized (singleThreadWaitHandle) {
            if (!singleThreadWaitHandle.isOn()) {
                singleThreadWaitHandle.countUp();
            }
        }
    }

    private void ensureSingleThreadWaitHandleIsOff() {
        synchronized (singleThreadWaitHandle) {
            singleThreadWaitHandle.countDown();
        }
    }

    private boolean countUpOrWaitForAwaitOverride()
            throws InterruptedException {
        synchronized (awaitOverrideWaitHandle) {
            awaitOverrideWaitHandle.await();
            awaitOverrideWaitHandle.countUp();

            return singleThreadWaitHandle.isOn();
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
                countUpOrWaitForAwaitOverride();
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
        boolean isSingleThreadWaiting;

        try {
            isSingleThreadWaiting = countUpOrWaitForAwaitOverride();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new RuntimeException("thread was interrupted", e);
        }

        if (isSingleThreadWaiting && timeout > 0L) {
            latestAwaitOverride.set(new TimeUnitPair(timeout, unit));
        } else {
            latestAwaitOverride.set(null);
        }

        releaseOneAwaitOverride();
        ensureSingleThreadWaitHandleIsOff();
    }

    @Override
    public void release() {
        latestAwaitOverride.set(null);
        ensureSingleThreadWaitHandleIsOff();
    }
}
