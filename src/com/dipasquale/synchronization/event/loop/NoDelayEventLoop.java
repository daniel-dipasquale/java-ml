package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.common.time.ZeroDateTimeSupport;
import com.dipasquale.synchronization.wait.handle.InteractiveWaitHandle;
import com.google.common.collect.ImmutableMap;

import javax.measure.quantity.Duration;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

final class NoDelayEventLoop implements EventLoop {
    private static final Map<Unit<Duration>, ZeroDateTimeSupport> ZERO_DATE_TIME_SUPPORTS = ImmutableMap.<Unit<Duration>, ZeroDateTimeSupport>builder()
            .put(SI.NANO(SI.SECOND), new ZeroDateTimeSupport(SI.NANO(SI.SECOND)))
            .put(SI.MICRO(SI.SECOND), new ZeroDateTimeSupport(SI.MICRO(SI.SECOND)))
            .put(SI.MILLI(SI.SECOND), new ZeroDateTimeSupport(SI.MILLI(SI.SECOND)))
            .put(SI.SECOND, new ZeroDateTimeSupport(SI.SECOND))
            .put(NonSI.MINUTE, new ZeroDateTimeSupport(NonSI.MINUTE))
            .put(NonSI.HOUR, new ZeroDateTimeSupport(NonSI.HOUR))
            .put(NonSI.DAY, new ZeroDateTimeSupport(NonSI.DAY))
            .build();

    private final ExplicitDelayEventLoop eventLoop;

    NoDelayEventLoop(final String name, final EventLoopParams params, final EventLoop nextEntryPoint) {
        ExplicitDelayEventLoopParams paramsFixed = ExplicitDelayEventLoopParams.builder()
                .eventRecords(new LinkedList<>())
                .executorService(params.getExecutorService())
                .dateTimeSupport(ZERO_DATE_TIME_SUPPORTS.get(params.getDateTimeSupport().unit()))
                .errorHandler(params.getErrorHandler())
                .build();

        this.eventLoop = new ExplicitDelayEventLoop(name, paramsFixed, nextEntryPoint);
    }

    @Override
    public String getName() {
        return eventLoop.getName();
    }

    @Override
    public int getConcurrencyLevel() {
        return eventLoop.getConcurrencyLevel();
    }

    @Override
    public void queue(final EventLoopHandler handler, final long delayTime, final ErrorHandler errorHandler, final InteractiveWaitHandle invokedWaitHandle) {
        eventLoop.queue(handler, 0L, errorHandler, invokedWaitHandle);
    }

    @Override
    public void queue(final IntervalEventLoopHandler handler, final long delayTime, final ErrorHandler errorHandler, final InteractiveWaitHandle invokedWaitHandle) {
        eventLoop.queue(handler, 0L, errorHandler, invokedWaitHandle);
    }

    @Override
    public boolean isEmpty() {
        return eventLoop.isEmpty();
    }

    @Override
    public void awaitUntilEmpty()
            throws InterruptedException {
        eventLoop.awaitUntilEmpty();
    }

    @Override
    public boolean awaitUntilEmpty(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return eventLoop.awaitUntilEmpty(timeout, unit);
    }

    @Override
    public void shutdown() {
        eventLoop.shutdown();
    }

    @Override
    public String toString() {
        return eventLoop.toString();
    }
}
