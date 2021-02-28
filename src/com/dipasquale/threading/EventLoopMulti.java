package com.dipasquale.threading;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.MultiExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

final class EventLoopMulti implements EventLoop {
    private final List<EventLoop> eventLoops;
    private final EventLoop.Selector eventLoopSelector;
    private final MultiWaitHandle waitUntilEmptyEventLoopsHandle;
    private final MultiExceptionHandler shutdownEventLoopsHandler;

    EventLoopMulti(final EventLoop.Factory eventLoopFactory, final EventLoop.Selector eventLoopSelector, final DateTimeSupport dateTimeSupport) {
        List<EventLoop> eventLoops = createEventLoops(eventLoopFactory, eventLoopSelector.size(), this);

        this.eventLoops = eventLoops;
        this.eventLoopSelector = eventLoopSelector;
        this.waitUntilEmptyEventLoopsHandle = MultiWaitHandle.create(dateTimeSupport, a -> !isEmpty(), eventLoops, EventLoop::awaitUntilEmpty, EventLoop::awaitUntilEmpty);
        this.shutdownEventLoopsHandler = MultiExceptionHandler.create(eventLoops, EventLoop::shutdown);
    }

    private static List<EventLoop> createEventLoops(final EventLoop.Factory eventLoopFactory, final int count, final EventLoopMulti eventLoop) {
        List<EventLoop> eventLoops = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            eventLoops.add(eventLoopFactory.create(eventLoop));
        }

        return eventLoops;
    }

    private EventLoop getNextEventLoop() {
        int index = eventLoopSelector.next();

        return eventLoops.get(index);
    }

    @Override
    public void queue(final Runnable handler, final long delayTime) {
        getNextEventLoop().queue(handler, delayTime);
    }

    @Override
    public void queue(final EventLoop.Handler handler) {
        getNextEventLoop().queue(handler);
    }

    @Override
    public boolean isEmpty() {
        return eventLoops.stream().allMatch(EventLoop::isEmpty);
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
        shutdownEventLoopsHandler.invokeAllAndThrowAsSuppressedIfAny("unable to shutdown the event loops");
    }
}
