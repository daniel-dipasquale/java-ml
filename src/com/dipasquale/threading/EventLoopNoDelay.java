package com.dipasquale.threading;

import com.dipasquale.common.ArgumentValidatorUtils;
import com.dipasquale.common.DateTimeSupport;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

final class EventLoopNoDelay implements EventLoop {
    private final EventLoopDefault eventLoop;

    EventLoopNoDelay(final String name, final EventLoopRecordQueueFactory eventRecordsFactory, final EventLoopDefaultParams params, final EventLoop nextEventLoop) {
        Queue<EventLoopRecord> queue = new LinkedList<>();
        ExclusiveQueue<EventLoopRecord> eventRecords = eventRecordsFactory.create(queue);

        EventLoopDefaultParams paramsFixed = EventLoopDefaultParams.builder()
                .dateTimeSupport(DateTimeSupport.create(() -> 0L, params.getDateTimeSupport().unit()))
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
