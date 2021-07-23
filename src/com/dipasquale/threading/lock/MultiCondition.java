package com.dipasquale.threading.lock;

import com.dipasquale.common.error.IterableErrorHandler;
import com.dipasquale.threading.wait.handle.MultiWaitHandle;

import javax.measure.converter.UnitConverter;
import javax.measure.unit.SI;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

final class MultiCondition implements Condition {
    private static final UnitConverter FROM_MS_TO_NS_UNIT_CONVERTER = SI.MILLI(SI.SECOND).getConverterTo(LockConstants.DATE_TIME_SUPPORT_NANOSECONDS.unit());
    private static final TimeUnit NS_TIME_UNIT = LockConstants.DATE_TIME_SUPPORT_NANOSECONDS.timeUnit();
    private final List<Condition> conditions;
    private final IterableErrorHandler<Condition> waitHandle;
    private final MultiWaitHandle timedWaitHandle;

    MultiCondition(final List<Condition> conditions) {
        this.conditions = conditions;
        this.waitHandle = new IterableErrorHandler<>(conditions, Condition::await);
        this.timedWaitHandle = new MultiWaitHandle(LockConstants.DATE_TIME_SUPPORT_NANOSECONDS, ConditionWaitHandle.translate(conditions));
    }

    @Override
    public void await()
            throws InterruptedException {
        waitHandle.invokeAllAndReportAsSuppressed(() -> new InterruptedException("unable to await on all conditions"));
    }

    @Override
    public void awaitUninterruptibly() {
        conditions.forEach(Condition::awaitUninterruptibly);
    }

    @Override
    public long awaitNanos(final long nanosTimeout)
            throws InterruptedException {
        long startDateTime = LockConstants.DATE_TIME_SUPPORT_NANOSECONDS.now();

        timedWaitHandle.await(nanosTimeout, NS_TIME_UNIT);

        return nanosTimeout - LockConstants.DATE_TIME_SUPPORT_NANOSECONDS.now() + startDateTime;
    }

    @Override
    public boolean await(final long time, final TimeUnit unit)
            throws InterruptedException {
        return timedWaitHandle.await(time, unit);
    }

    @Override
    public boolean awaitUntil(final Date deadline)
            throws InterruptedException {
        long deadlineDateTime = (long) FROM_MS_TO_NS_UNIT_CONVERTER.convert((double) deadline.getTime());
        long currentDateTime = LockConstants.DATE_TIME_SUPPORT_NANOSECONDS.now();
        long time = Math.max(deadlineDateTime - currentDateTime, 0L);

        return timedWaitHandle.await(time, NS_TIME_UNIT);
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
