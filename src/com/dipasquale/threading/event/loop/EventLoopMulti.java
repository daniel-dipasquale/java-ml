package com.dipasquale.threading.event.loop;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.ErrorLogger;
import com.dipasquale.common.MultiExceptionHandler;
import com.dipasquale.threading.wait.handle.MultiWaitHandle;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

final class EventLoopMulti implements EventLoop {
    @Getter
    private final String name;
    private final List<EventLoop> eventLoops;
    private final EventLoopSelector eventLoopSelector;
    private final MultiWaitHandle waitUntilEmptyHandler;
    private final MultiExceptionHandler<EventLoop> shutdownHandler;

    EventLoopMulti(final String name, final EventLoopFactory eventLoopFactory, final EventLoopSelector eventLoopSelector, final DateTimeSupport dateTimeSupport) {
        List<EventLoop> eventLoops = createEventLoops(eventLoopFactory, eventLoopSelector.size(), this);

        this.name = name;
        this.eventLoops = eventLoops;
        this.eventLoopSelector = eventLoopSelector;
        this.waitUntilEmptyHandler = new MultiWaitHandle(dateTimeSupport, a -> !isEmpty(), EventLoopWaitHandle.translate(eventLoops));
        this.shutdownHandler = new MultiExceptionHandler<>(eventLoops, EventLoop::shutdown);
    }

    private static List<EventLoop> createEventLoops(final EventLoopFactory eventLoopFactory, final int count, final EventLoopMulti eventLoopOwner) {
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
    public void queue(final EventLoopHandler handler, final long delayTime, final ErrorLogger errorLogger, final CountDownLatch countDownLatch) {
        getNextEventLoop().queue(handler, delayTime, errorLogger, countDownLatch);
    }

    @Override
    public void queue(final EventLoopIntervalHandler handler, final long delayTime, final ErrorLogger errorLogger, final CountDownLatch countDownLatch) {
        getNextEventLoop().queue(handler, delayTime, errorLogger, countDownLatch);
    }

    @Override
    public boolean isEmpty() {
        return eventLoops.stream()
                .allMatch(EventLoop::isEmpty);
    }

    @Override
    public void awaitUntilEmpty()
            throws InterruptedException {
        waitUntilEmptyHandler.await();
    }

    @Override
    public boolean awaitUntilEmpty(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return waitUntilEmptyHandler.await(timeout, unit);
    }

    @Override
    public void shutdown() {
        shutdownHandler.invokeAllAndReportAsSuppressed("unable to shutdown the event loops");
    }

    @Override
    public String toString() {
        return name;
    }
}
