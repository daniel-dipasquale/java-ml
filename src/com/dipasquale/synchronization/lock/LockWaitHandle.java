package com.dipasquale.synchronization.lock;

import com.dipasquale.synchronization.wait.handle.WaitHandle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class LockWaitHandle implements WaitHandle {
    private final Lock lock;

    @Override
    public void await()
            throws InterruptedException {
        lock.lockInterruptibly();
    }

    @Override
    public boolean await(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return lock.tryLock(timeout, unit);
    }
}
