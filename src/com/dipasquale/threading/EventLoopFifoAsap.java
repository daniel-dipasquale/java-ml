package com.dipasquale.threading;

import com.dipasquale.common.ArgumentValidator;
import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.ExceptionLogger;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

final class EventLoopFifoAsap implements EventLoop {
    private final EventLoopDefault eventLoop;

    EventLoopFifoAsap(final DateTimeSupport dateTimeSupport, final String name, final ExceptionLogger exceptionLogger, final EventLoop nextLoop, final ExecutorService executorService) {
        Queue<EventLoopDefault.Record> eventHandlers = new LinkedList<>();

        this.eventLoop = new EventLoopDefault(eventHandlers, dateTimeSupport, name, exceptionLogger, nextLoop, executorService);
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
    public void queue(final Handler handler) {
        ensureDelayTimeIsValid(handler.getDelayTime());

        eventLoop.queue(new Handler() {
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
