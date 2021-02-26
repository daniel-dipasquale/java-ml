package com.dipasquale.threading;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.MultiExceptionHandler;

import javax.measure.converter.UnitConverter;
import javax.measure.unit.SI;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

final class MultiCondition implements Condition {
    private static final DateTimeSupport DATE_TIME_SUPPORT = DateTimeSupport.createNanoseconds();
    private static final UnitConverter FROM_MS_TO_NS_UNIT_CONVERTER = SI.MILLI(SI.SECOND).getConverterTo(DATE_TIME_SUPPORT.unit());
    private static final TimeUnit NS_TIME_UNIT = DATE_TIME_SUPPORT.timeUnit();
    private final List<Condition> conditions;
    private final MultiWaitHandle waitConditionsHandle;
    private final MultiExceptionHandler awaitConditionsHandler;

    MultiCondition(final List<Condition> conditions) {
        this.conditions = conditions;
        this.waitConditionsHandle = MultiWaitHandle.createSinglePass(DATE_TIME_SUPPORT, conditions, null, Condition::await);
        this.awaitConditionsHandler = MultiExceptionHandler.create(conditions, Condition::await);
    }

    @Override
    public void await()
            throws InterruptedException {
        awaitConditionsHandler.invokeAllAndThrowAsSuppressedIfAny(() -> new InterruptedException("unable to await on all conditions"));
    }

    @Override
    public void awaitUninterruptibly() {
        conditions.forEach(Condition::awaitUninterruptibly);
    }

    @Override
    public long awaitNanos(final long nanosTimeout)
            throws InterruptedException {
        long startDateTime = DATE_TIME_SUPPORT.now();

        waitConditionsHandle.await(nanosTimeout, NS_TIME_UNIT);

        return nanosTimeout - DATE_TIME_SUPPORT.now() + startDateTime;
    }

    @Override
    public boolean await(final long time, final TimeUnit unit)
            throws InterruptedException {
        return waitConditionsHandle.await(time, unit);
    }

    @Override
    public boolean awaitUntil(final Date deadline)
            throws InterruptedException {
        long deadlineDateTime = (long) FROM_MS_TO_NS_UNIT_CONVERTER.convert((double) deadline.getTime());
        long currentDateTime = DATE_TIME_SUPPORT.now();
        long time = Math.max(deadlineDateTime - currentDateTime, 0L);

        return waitConditionsHandle.await(time, NS_TIME_UNIT);
    }

    @Override
    public void signal() {
        conditions.forEach(Condition::signal);
    }

    @Override
    public void signalAll() {
        conditions.forEach(Condition::signalAll);
    }
}
