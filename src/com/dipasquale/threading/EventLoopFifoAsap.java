package com.dipasquale.threading;

import com.dipasquale.common.ArgumentValidator;
import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.ExceptionLogger;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

final class EventLoopFifoAsap implements EventLoop {
    private final EventLoopDefault eventLoop;

    EventLoopFifoAsap(final DateTimeSupport dateTimeSupport, final String name, final ExceptionLogger exceptionLogger, final EventLoop nextEventLoop, final ExecutorService executorService) {
        ExclusiveQueue<EventLoop.Record> eventRecords = new ExclusiveQueueLocked<>(new ReentrantLock(), new LinkedList<>());

        this.eventLoop = new EventLoopDefault(eventRecords, dateTimeSupport, name, exceptionLogger, nextEventLoop, executorService);
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
