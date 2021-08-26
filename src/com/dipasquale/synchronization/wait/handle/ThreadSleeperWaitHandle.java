package com.dipasquale.synchronization.wait.handle;

import com.dipasquale.common.time.DateTimeSupport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.measure.converter.UnitConverter;
import javax.measure.quantity.Duration;
import javax.measure.unit.Unit;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ThreadSleeperWaitHandle implements WaitHandle {
    private static final Unit<Duration> UNIT = DateTimeSupport.getUnit(TimeUnit.MILLISECONDS);
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
    public boolean await(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        UnitConverter unitConverter = DateTimeSupport.getUnit(unit).getConverterTo(UNIT);
        double timeoutFixed = unitConverter.convert((double) timeout);

        Thread.sleep((long) timeoutFixed);

        return true;
    }
}
