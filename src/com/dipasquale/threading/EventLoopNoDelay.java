package com.dipasquale.threading;

import com.dipasquale.common.ArgumentValidator;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

final class EventLoopNoDelay implements EventLoop {
    private final EventLoopDefault eventLoop;

    EventLoopNoDelay(final EventLoopDefault.Params params, final EventLoopDefault.EventRecordsFactory eventRecordsFactory, final String name, final EventLoop nextEventLoop) {
        Queue<Record> queue = new LinkedList<>();
        ExclusiveQueue<EventLoop.Record> eventRecords = eventRecordsFactory.create(queue);

        this.eventLoop = new EventLoopDefault(eventRecords, params, name, nextEventLoop);
    }

    private static void ensureDelayTimeIsValid(final long delayTime) {
        ArgumentValidator.getInstance().ensureEqual(delayTime, 0L, "delayTime", "must be 0 for FIFO ASAP event loops");
    }

    @Override
    public void queue(final Runnable handler, final long delayTime) {
        ensureDelayTimeIsValid(delayTime);
        eventLoop.queue(handler, 0L);
    }

    @Override
    public void queue(final EventLoop.Handler handler) {
        ensureDelayTimeIsValid(handler.getDelayTime());

        eventLoop.queue(new EventLoop.Handler() {
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
