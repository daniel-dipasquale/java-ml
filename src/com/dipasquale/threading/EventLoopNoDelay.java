package com.dipasquale.threading;

import com.dipasquale.common.ArgumentValidatorUtils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

final class EventLoopNoDelay implements EventLoop {
    private final EventLoopDefault eventLoop;

    EventLoopNoDelay(final EventLoopDefaultParams params, final EventLoopRecordQueueFactory eventRecordsFactory, final String name, final EventLoop nextEventLoop) {
        Queue<EventLoopRecord> queue = new LinkedList<>();
        ExclusiveQueue<EventLoopRecord> eventRecords = eventRecordsFactory.create(queue);

        this.eventLoop = new EventLoopDefault(eventRecords, params, name, nextEventLoop);
    }

    private static void ensureDelayTimeIsValid(final long delayTime) {
        ArgumentValidatorUtils.ensureEqual(delayTime, 0L, "delayTime", "must be 0 for FIFO ASAP event loops");
    }

    @Override
    public void queue(final Runnable handler, final long delayTime) {
        ensureDelayTimeIsValid(delayTime);
        eventLoop.queue(handler, 0L);
    }

    @Override
    public void queue(final EventLoopHandler handler) {
        ensureDelayTimeIsValid(handler.getDelayTime());

        eventLoop.queue(new EventLoopHandler() {
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
            public void handle() {
                handler.handle();
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
}
