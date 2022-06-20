package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.common.time.ZeroDateTimeSupport;
import com.dipasquale.synchronization.wait.handle.InteractiveWaitHandle;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

final class ZeroDelayEventLoop implements EventLoop {
    private static final Map<TimeUnit, ZeroDateTimeSupport> ZERO_DATE_TIME_SUPPORTS = createZeroDateTimeSupports();
    private static final int DELAY_TIME = 0;
    private final ExplicitDelayEventLoop eventLoop;

    private static Map<TimeUnit, ZeroDateTimeSupport> createZeroDateTimeSupports() {
        Map<TimeUnit, ZeroDateTimeSupport> zeroDateTimeSupports = new EnumMap<>(TimeUnit.class);

        zeroDateTimeSupports.put(TimeUnit.NANOSECONDS, new ZeroDateTimeSupport(TimeUnit.NANOSECONDS));
        zeroDateTimeSupports.put(TimeUnit.MICROSECONDS, new ZeroDateTimeSupport(TimeUnit.MICROSECONDS));
        zeroDateTimeSupports.put(TimeUnit.MILLISECONDS, new ZeroDateTimeSupport(TimeUnit.MILLISECONDS));
        zeroDateTimeSupports.put(TimeUnit.SECONDS, new ZeroDateTimeSupport(TimeUnit.SECONDS));
        zeroDateTimeSupports.put(TimeUnit.MINUTES, new ZeroDateTimeSupport(TimeUnit.MINUTES));
        zeroDateTimeSupports.put(TimeUnit.HOURS, new ZeroDateTimeSupport(TimeUnit.HOURS));
        zeroDateTimeSupports.put(TimeUnit.DAYS, new ZeroDateTimeSupport(TimeUnit.DAYS));

        return zeroDateTimeSupports;
    }

    ZeroDelayEventLoop(final EventLoopParams params, final EventLoop entryPoint) {
        ExplicitDelayEventLoopParams fixedParams = ExplicitDelayEventLoopParams.builder()
                .eventRecords(new LinkedList<>())
                .executorService(params.getExecutorService())
                .dateTimeSupport(ZERO_DATE_TIME_SUPPORTS.get(params.getDateTimeSupport().timeUnit()))
                .errorHandler(params.getErrorHandler())
                .build();

        this.eventLoop = new ExplicitDelayEventLoop(fixedParams, entryPoint);
    }

    @Override
    public List<Long> getThreadIds() {
        return eventLoop.getThreadIds();
    }

    @Override
    public void queue(final EventLoopHandler handler, final long delayTime, final ErrorHandler errorHandler, final InteractiveWaitHandle invokedWaitHandle) {
        eventLoop.queue(handler, DELAY_TIME, errorHandler, invokedWaitHandle);
    }

    @Override
    public void queue(final IntervalEventLoopHandler handler, final long delayTime, final ErrorHandler errorHandler, final InteractiveWaitHandle invokedWaitHandle) {
        eventLoop.queue(handler, DELAY_TIME, errorHandler, invokedWaitHandle);
    }

    @Override
    public boolean isEmpty() {
        return eventLoop.isEmpty();
    }

    @Override
    public void await()
            throws InterruptedException {
        eventLoop.await();
    }

    @Override
    public boolean await(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return eventLoop.await(timeout, unit);
    }

    @Override
    public void clear() {
        eventLoop.clear();
    }

    @Override
    public void shutdown() {
        eventLoop.shutdown();
    }
}
