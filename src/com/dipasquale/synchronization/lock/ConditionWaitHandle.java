package com.dipasquale.synchronization.lock;

import com.dipasquale.synchronization.wait.handle.WaitHandle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ConditionWaitHandle implements WaitHandle {
    private final Condition condition;

    @Override
    public void await()
            throws InterruptedException {
        condition.await();
    }

    @Override
    public boolean await(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return condition.await(timeout, unit);
    }
}
