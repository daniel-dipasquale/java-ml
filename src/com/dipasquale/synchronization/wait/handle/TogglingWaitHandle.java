package com.dipasquale.synchronization.wait.handle;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class TogglingWaitHandle implements InteractiveWaitHandle {
    private final AtomicBoolean locked = new AtomicBoolean();
    private final ReusableCountingWaitHandle waitHandle = new ReusableCountingWaitHandle(0);

    public boolean isOn() {
        return locked.get();
    }

    @Override
    public void countUp() {
        if (locked.compareAndSet(false, true)) {
            waitHandle.countUp();
        }
    }

    @Override
    public void countDown() {
        if (locked.compareAndSet(true, false)) {
            waitHandle.countDown();
        }
    }

    @Override
    public void await()
            throws InterruptedException {
        waitHandle.await();
    }

    @Override
    public boolean await(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return waitHandle.await(timeout, unit);
    }
}
