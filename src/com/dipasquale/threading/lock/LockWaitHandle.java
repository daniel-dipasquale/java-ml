package com.dipasquale.threading.lock;

import com.dipasquale.threading.wait.handle.WaitHandle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

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

    static List<LockWaitHandle> translate(final List<? extends Lock> locks) {
        return locks.stream()
                .map(LockWaitHandle::new)
                .collect(Collectors.toList());
    }
}
