package com.dipasquale.synchronization.wait.handle;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class TogglingWaitHandle implements InteractiveWaitHandle, Serializable {
    @Serial
    private static final long serialVersionUID = 1725802563538248576L;
    private final AtomicBoolean locked = new AtomicBoolean();
    private final ReusableCountDownWaitHandle waitHandle = new ReusableCountDownWaitHandle(0, WaitCondition.ON_NOT_ZERO);

    public boolean isOn() {
        return locked.get();
    }

    @Override
    public boolean countUp() {
        if (!locked.compareAndSet(false, true)) {
            return false;
        }

        waitHandle.countUp();

        return true;
    }

    @Override
    public boolean countDown() {
        if (!locked.compareAndSet(true, false)) {
            return false;
        }

        waitHandle.countDown();

        return true;
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
