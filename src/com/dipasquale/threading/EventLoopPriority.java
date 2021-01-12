package com.dipasquale.threading;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.ExceptionLogger;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
final class EventLoopPriority implements EventLoop {
    private final EventLoopDefault eventLoop;

    EventLoopPriority(final DateTimeSupport dateTimeSupport, final String name, final ExceptionLogger exceptionLogger, final EventLoop next, final ExecutorService executorService) {
        Queue<EventLoopDefault.Record> eventHandlers = new PriorityQueue<>(Comparator.comparing(EventLoopDefault.Record::getExecutionDateTime));

        this.eventLoop = new EventLoopDefault(eventHandlers, dateTimeSupport, name, exceptionLogger, next, executorService);
    }

    @Override
    public void queue(final Runnable handler, final long delayTime) {
        eventLoop.queue(handler, delayTime);
    }

    @Override
    public void queue(final Handler handler) {
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
