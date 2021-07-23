package com.dipasquale.threading.event.loop;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.error.ErrorLogger;
import com.dipasquale.common.time.DateTimeSupport;
import com.google.common.collect.ImmutableMap;

import javax.measure.quantity.Duration;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

final class EventLoopNoDelay implements EventLoop {
    private static final Map<Unit<Duration>, DateTimeSupport> DATE_TIME_SUPPORTS = ImmutableMap.<Unit<Duration>, DateTimeSupport>builder()
            .put(SI.NANO(SI.SECOND), DateTimeSupport.createZero(SI.NANO(SI.SECOND)))
            .put(SI.MICRO(SI.SECOND), DateTimeSupport.createZero(SI.MICRO(SI.SECOND)))
            .put(SI.MILLI(SI.SECOND), DateTimeSupport.createZero(SI.MILLI(SI.SECOND)))
            .put(SI.SECOND, DateTimeSupport.createZero(SI.SECOND))
            .put(NonSI.MINUTE, DateTimeSupport.createZero(NonSI.MINUTE))
            .put(NonSI.HOUR, DateTimeSupport.createZero(NonSI.HOUR))
            .put(NonSI.DAY, DateTimeSupport.createZero(NonSI.DAY))
            .build();

    private final EventLoopDefault eventLoop;

    EventLoopNoDelay(final String name, final EventLoopRecordQueueFactory eventRecordsFactory, final EventLoopDefaultParams params, final EventLoop nextEventLoop) {
        Queue<EventLoopRecord> queue = new LinkedList<>();
        ExclusiveQueue<EventLoopRecord> eventRecords = eventRecordsFactory.create(queue);

        EventLoopDefaultParams paramsFixed = EventLoopDefaultParams.builder()
                .executorService(params.getExecutorService())
                .dateTimeSupport(DATE_TIME_SUPPORTS.get(params.getDateTimeSupport().unit()))
                .errorLogger(params.getErrorLogger())
                .build();

        this.eventLoop = new EventLoopDefault(name, eventRecords, paramsFixed, nextEventLoop);
    }

    @Override
    public String getName() {
        return eventLoop.getName();
    }

    @Override
    public int getConcurrencyLevel() {
        return eventLoop.getConcurrencyLevel();
    }

    private static void ensureDelayTimeIsValid(final long delayTime) {
        ArgumentValidatorSupport.ensureEqual(delayTime, 0L, "delayTime", "must be 0 for FIFO ASAP event loops");
    }

    @Override
    public void queue(final EventLoopHandler handler, final long delayTime, final ErrorLogger errorLogger, final CountDownLatch countDownLatch) {
        ensureDelayTimeIsValid(delayTime);
        eventLoop.queue(handler, delayTime, errorLogger, countDownLatch);
    }

    @Override
    public void queue(final EventLoopIntervalHandler handler, final long delayTime, final ErrorLogger errorLogger, final CountDownLatch countDownLatch) {
        ensureDelayTimeIsValid(delayTime);
        eventLoop.queue(handler, delayTime, errorLogger, countDownLatch);
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
