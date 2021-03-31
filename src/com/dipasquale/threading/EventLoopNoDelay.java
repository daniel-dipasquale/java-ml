package com.dipasquale.threading;

import com.dipasquale.common.ArgumentValidatorUtils;
import com.dipasquale.common.DateTimeSupport;
import com.google.common.collect.ImmutableMap;

import javax.measure.quantity.Duration;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

final class EventLoopNoDelay implements EventLoop {
    private static final Map<Unit<Duration>, DateTimeSupport> DATE_TIME_SUPPORTS = ImmutableMap.<Unit<Duration>, DateTimeSupport>builder()
            .put(SI.NANO(SI.SECOND), DateTimeSupport.create(() -> 0L, SI.NANO(SI.SECOND)))
            .put(SI.MICRO(SI.SECOND), DateTimeSupport.create(() -> 0L, SI.MICRO(SI.SECOND)))
            .put(SI.MILLI(SI.SECOND), DateTimeSupport.create(() -> 0L, SI.MILLI(SI.SECOND)))
            .put(SI.SECOND, DateTimeSupport.create(() -> 0L, SI.SECOND))
            .put(NonSI.MINUTE, DateTimeSupport.create(() -> 0L, NonSI.MINUTE))
            .put(NonSI.HOUR, DateTimeSupport.create(() -> 0L, NonSI.HOUR))
            .put(NonSI.DAY, DateTimeSupport.create(() -> 0L, NonSI.DAY))
            .build();

    private final EventLoopDefault eventLoop;

    EventLoopNoDelay(final String name, final EventLoopRecordQueueFactory eventRecordsFactory, final EventLoopDefaultParams params, final EventLoop nextEventLoop) {
        Queue<EventLoopRecord> queue = new LinkedList<>();
        ExclusiveQueue<EventLoopRecord> eventRecords = eventRecordsFactory.create(queue);

        EventLoopDefaultParams paramsFixed = EventLoopDefaultParams.builder()
                .dateTimeSupport(DATE_TIME_SUPPORTS.get(params.getDateTimeSupport().unit()))
                .exceptionLogger(params.getExceptionLogger())
                .executorService(params.getExecutorService())
                .build();

        this.eventLoop = new EventLoopDefault(name, eventRecords, paramsFixed, nextEventLoop);
    }

    @Override
    public String getName() {
        return eventLoop.getName();
    }

    private static void ensureDelayTimeIsValid(final long delayTime) {
        ArgumentValidatorUtils.ensureEqual(delayTime, 0L, "delayTime", "must be 0 for FIFO ASAP event loops");
    }

    @Override
    public void queue(final EventLoopHandler handler, final long delayTime) {
        ensureDelayTimeIsValid(delayTime);
        eventLoop.queue(handler, 0L);
    }

    @Override
    public void queue(final EventLoopQueueableHandler handler) {
        ensureDelayTimeIsValid(handler.getDelayTime());

        eventLoop.queue(new EventLoopQueueableHandler() {
            @Override
            public boolean shouldReQueue() {
                return handler.shouldReQueue();
            }

            @Override
            public long getDelayTime() {
                ensureDelayTimeIsValid(handler.getDelayTime());

                return 0L;
            }

            @Override
            public void handle(final String name) {
                handler.handle(name);
            }
        });
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
