package com.dipasquale.threading.event.loop;

import com.dipasquale.common.error.ErrorLogger;
import com.dipasquale.common.error.IterableErrorHandler;
import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.threading.wait.handle.MultiWaitHandle;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

final class MultiEventLoop implements EventLoop {
    @Getter
    private final String name;
    private final List<EventLoop> eventLoops;
    private final EventLoopSelector eventLoopSelector;
    private final MultiWaitHandle waitUntilEmptyEventLoopsHandle;
    private final IterableErrorHandler<EventLoop> shutdownEventLoopsHandler;

    MultiEventLoop(final String name, final SingleEventLoopFactoryProxy eventLoopFactory, final EventLoopSelector eventLoopSelector, final DateTimeSupport dateTimeSupport) {
        List<EventLoop> eventLoops = createEventLoops(eventLoopFactory, eventLoopSelector.size(), this);

        this.name = name;
        this.eventLoops = eventLoops;
        this.eventLoopSelector = eventLoopSelector;
        this.waitUntilEmptyEventLoopsHandle = MultiWaitHandle.create(eventLoops, EventLoopWaitHandle::new, dateTimeSupport, a -> !isEmpty());
        this.shutdownEventLoopsHandler = new IterableErrorHandler<>(eventLoops, EventLoop::shutdown);
    }

    private static List<EventLoop> createEventLoops(final SingleEventLoopFactoryProxy eventLoopFactory, final int count, final MultiEventLoop eventLoopOwner) {
        List<EventLoop> eventLoops = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            eventLoops.add(eventLoopFactory.create(eventLoopOwner));
        }

        return eventLoops;
    }

    @Override
    public int getConcurrencyLevel() {
        return eventLoops.size();
    }

    private EventLoop getNextEventLoop() {
        int index = eventLoopSelector.next();

        return eventLoops.get(index);
    }

    @Override
    public void queue(final EventLoopHandler handler, final long delayTime, final ErrorLogger errorLogger, final CountDownLatch invokedCountDownLatch) {
        getNextEventLoop().queue(handler, delayTime, errorLogger, invokedCountDownLatch);
    }

    @Override
    public void queue(final IntervalEventLoopHandler handler, final long delayTime, final ErrorLogger errorLogger, final CountDownLatch invokedCountDownLatch) {
        getNextEventLoop().queue(handler, delayTime, errorLogger, invokedCountDownLatch);
    }

    @Override
    public boolean isEmpty() {
        return eventLoops.stream()
                .allMatch(EventLoop::isEmpty);
    }

    @Override
    public void awaitUntilEmpty()
            throws InterruptedException {
        waitUntilEmptyEventLoopsHandle.await();
    }

    @Override
    public boolean awaitUntilEmpty(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return waitUntilEmptyEventLoopsHandle.await(timeout, unit);
    }

    @Override
    public void shutdown() {
        shutdownEventLoopsHandler.handleAll("unable to shutdown the event loops");
    }

    @Override
    public String toString() {
        return name;
    }
}
