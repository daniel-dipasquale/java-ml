package com.dipasquale.threading.wait.handle;

import com.dipasquale.common.DateTimeSupport;

import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;
import java.util.concurrent.TimeUnit;

@FunctionalInterface
public interface WaitHandle {
    void await(long timeout, TimeUnit unit) throws InterruptedException;

    static WaitHandle createThreadSleeper() {
        Unit<Duration> unit = DateTimeSupport.getUnit(TimeUnit.MILLISECONDS);

        return (t, u) -> Thread.sleep((long) DateTimeSupport.getUnit(u).getConverterTo(unit).convert((double) t));
    }
}
