/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

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
    private static final UnitConverter FROM_MS_TO_NS_UNIT_CONVERTER = SI.MILLI(SI.SECOND).getConverterTo(Constants.DATE_TIME_SUPPORT_NANOSECONDS.unit());
    private static final TimeUnit NS_TIME_UNIT = Constants.DATE_TIME_SUPPORT_NANOSECONDS.timeUnit();
    private final List<Condition> conditions;
    private final IterableErrorHandler<Condition> conditionsAwaitHandler;
    private final MultiWaitHandle conditionsWaitHandleUntilTimeout;

    MultiCondition(final List<Condition> conditions) {
        this.conditions = conditions;
        this.conditionsAwaitHandler = new IterableErrorHandler<>(conditions, Condition::await);
        this.conditionsWaitHandleUntilTimeout = MultiWaitHandle.create(conditions, ConditionWaitHandle::new, Constants.DATE_TIME_SUPPORT_NANOSECONDS);
    }

    @Override
    public void await()
            throws InterruptedException {
        conditionsAwaitHandler.handleAll(() -> new InterruptedException("unable to await on all conditions"));
    }

    @Override
    public void awaitUninterruptibly() {
        conditions.forEach(Condition::awaitUninterruptibly);
    }

    @Override
    public long awaitNanos(final long nanosTimeout)
            throws InterruptedException {
        long startDateTime = Constants.DATE_TIME_SUPPORT_NANOSECONDS.now();

        conditionsWaitHandleUntilTimeout.await(nanosTimeout, NS_TIME_UNIT);

        return nanosTimeout - Constants.DATE_TIME_SUPPORT_NANOSECONDS.now() + startDateTime;
    }

    @Override
    public boolean await(final long time, final TimeUnit unit)
            throws InterruptedException {
        return conditionsWaitHandleUntilTimeout.await(time, unit);
    }

    @Override
    public boolean awaitUntil(final Date deadline)
            throws InterruptedException {
        long deadlineDateTime = (long) FROM_MS_TO_NS_UNIT_CONVERTER.convert((double) deadline.getTime());
        long currentDateTime = Constants.DATE_TIME_SUPPORT_NANOSECONDS.now();
        long time = Math.max(deadlineDateTime - currentDateTime, 0L);

        return conditionsWaitHandleUntilTimeout.await(time, NS_TIME_UNIT);
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
