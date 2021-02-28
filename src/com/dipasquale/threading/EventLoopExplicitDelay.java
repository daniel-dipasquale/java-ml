package com.dipasquale.threading;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

final class EventLoopExplicitDelay implements EventLoop {
    private final EventLoopDefault eventLoop;

    EventLoopExplicitDelay(final EventLoopDefault.Params params, final EventLoopDefault.EventRecordsFactory eventRecordsFactory, final String name, final EventLoop nextEventLoop) {
        Queue<Record> queue = new PriorityQueue<>(Comparator.comparing(EventLoop.Record::getExecutionDateTime));
        ExclusiveQueue<EventLoop.Record> eventRecords = eventRecordsFactory.create(queue);

        this.eventLoop = new EventLoopDefault(eventRecords, params, name, nextEventLoop);
    }

    @Override
    public void queue(final Runnable handler, final long delayTime) {
        eventLoop.queue(handler, delayTime);
    }

    @Override
    public void queue(final EventLoop.Handler handler) {
        eventLoop.queue(handler);
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
