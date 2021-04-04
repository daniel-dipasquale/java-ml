package com.dipasquale.threading.wait.handle;

import com.dipasquale.common.DateTimeSupport;

import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;
import java.util.concurrent.TimeUnit;

public interface WaitHandle {
    void await() throws InterruptedException;

    boolean await(long timeout, TimeUnit unit) throws InterruptedException;

    static WaitHandle createThreadSleeper() {
        Unit<Duration> unit = DateTimeSupport.getUnit(TimeUnit.MILLISECONDS);

        return new WaitHandle() {
            @Override
            public void await() {
            }

            @Override
            public boolean await(final long timeout, final TimeUnit timeUnit)
                    throws InterruptedException {
                UnitConverter unitConverter = DateTimeSupport.getUnit(timeUnit).getConverterTo(unit);
                double timeoutFixed = unitConverter.convert((double) timeout);

                Thread.sleep((long) timeoutFixed);

                return true;
            }
        };
    }
}
