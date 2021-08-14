package com.dipasquale.threading.wait.handle;

import com.dipasquale.common.time.DateTimeSupport;

import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;
import java.util.concurrent.TimeUnit;

public final class SinglePassWaitHandle implements WaitHandle {
    private final ReusableLatch reusableLatch = new ReusableLatch(0);
    private boolean acquired = false;

    public boolean acquire() {
        synchronized (reusableLatch) {
            if (!acquired) {
                acquired = true;
                reusableLatch.countUp();

                return true;
            }

            return false;
        }
    }

    @Override
    public void await()
            throws InterruptedException {
        while (!acquire()) {
            reusableLatch.await();
        }
    }

    @Override
    public boolean await(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        boolean acquired = false;
        Unit<Duration> unitFixed = DateTimeSupport.getUnit(unit);
        UnitConverter unitConverter = unitFixed.getConverterTo(Constants.DATE_TIME_SUPPORT_NANOSECONDS.unit());
        long timeoutRemaining = (long) unitConverter.convert((double) timeout);
        long offsetDateTime = Constants.DATE_TIME_SUPPORT_NANOSECONDS.now();

        while (!acquire() && timeoutRemaining > 0L) {
            acquired = reusableLatch.await(timeoutRemaining, Constants.DATE_TIME_SUPPORT_NANOSECONDS.timeUnit());

            long currentDateTime = Constants.DATE_TIME_SUPPORT_NANOSECONDS.now();

            timeoutRemaining -= currentDateTime - offsetDateTime;
            offsetDateTime = currentDateTime;
        }

        return acquired && timeoutRemaining > 0L;
    }

    public void release() {
        synchronized (reusableLatch) {
            if (acquired) {
                acquired = false;
                reusableLatch.countDown();
            }
        }
    }
}
