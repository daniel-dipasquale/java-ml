package com.dipasquale.threading.wait.handle;

import com.dipasquale.common.DateTimeSupport;

import java.util.concurrent.TimeUnit;

public final class SinglePassWaitHandle {
    private final ReusableCountDownLatch reusableCountDownLatch = new ReusableCountDownLatch(0);
    private boolean acquired = false;

    public boolean acquire() {
        synchronized (reusableCountDownLatch) {
            if (!acquired) {
                acquired = true;
                reusableCountDownLatch.countUp();

                return true;
            }

            return false;
        }
    }

    public void acquireOrAwait()
            throws InterruptedException {
        while (!acquire()) {
            reusableCountDownLatch.await();
        }
    }

    public boolean acquireOrAwait(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        boolean acquired = false;
        long timeoutRemaining = (long) DateTimeSupport.getUnit(unit).getConverterTo(WaitHandleConstants.DATE_TIME_SUPPORT_NANOSECONDS.unit()).convert((double) timeout);
        long offsetDateTime = WaitHandleConstants.DATE_TIME_SUPPORT_NANOSECONDS.now();

        while (!acquire() && timeoutRemaining > 0L) {
            acquired = reusableCountDownLatch.await(timeoutRemaining, WaitHandleConstants.DATE_TIME_SUPPORT_NANOSECONDS.timeUnit());

            long currentDateTime = WaitHandleConstants.DATE_TIME_SUPPORT_NANOSECONDS.now();

            timeoutRemaining -= currentDateTime - offsetDateTime;
            offsetDateTime = currentDateTime;
        }

        return acquired && timeoutRemaining > 0L;
    }

    public void release() {
        synchronized (reusableCountDownLatch) {
            if (acquired) {
                acquired = false;
                reusableCountDownLatch.countDown();
            }
        }
    }
}