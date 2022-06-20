package com.dipasquale.synchronization.wait.handle;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public final class CountDownWaitHandle implements InteractiveWaitHandle {
    private final CountDownLatch countDownLatch;

    public CountDownWaitHandle(final int count) {
        this.countDownLatch = new CountDownLatch(count);
    }

    @Override
    public boolean countUp() {
        return false;
    }

    @Override
    public boolean countDown() {
        countDownLatch.countDown();

        return true;
    }

    @Override
    public void await()
            throws InterruptedException {
        countDownLatch.await();
    }

    @Override
    public boolean await(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return countDownLatch.await(timeout, unit);
    }
}
