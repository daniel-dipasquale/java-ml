package com.dipasquale.synchronization.wait.handle;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class ThreadSleeperWaitHandle implements WaitHandle {
    private static final ThreadSleeperWaitHandle INSTANCE = new ThreadSleeperWaitHandle();

    public static ThreadSleeperWaitHandle getInstance() {
        return INSTANCE;
    }

    @Override
    public void await()
            throws InterruptedException {
        Thread.currentThread().join();
    }

    @Override
    public boolean await(final long timeout, final TimeUnit timeUnit)
            throws InterruptedException {
        long fixedTimeout = TimeUnit.MILLISECONDS.convert(timeout, timeUnit);

        Thread.sleep(fixedTimeout);

        return true;
    }
}
