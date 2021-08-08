package com.dipasquale.threading.wait.handle;

import com.dipasquale.common.time.DateTimeSupport;

import java.util.concurrent.TimeUnit;

public final class SinglePassWaitHandle implements WaitHandle {
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

    @Override
    public void await()
            throws InterruptedException {
        while (!acquire()) {
            reusableCountDownLatch.await();
        }
    }

    @Override
    public boolean await(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        boolean acquired = false;
        long timeoutRemaining = (long) DateTimeSupport.getUnit(unit).getConverterTo(Constants.DATE_TIME_SUPPORT_NANOSECONDS.unit()).convert((double) timeout);
        long offsetDateTime = Constants.DATE_TIME_SUPPORT_NANOSECONDS.now();

        while (!acquire() && timeoutRemaining > 0L) {
            acquired = reusableCountDownLatch.await(timeoutRemaining, Constants.DATE_TIME_SUPPORT_NANOSECONDS.timeUnit());

            long currentDateTime = Constants.DATE_TIME_SUPPORT_NANOSECONDS.now();

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
