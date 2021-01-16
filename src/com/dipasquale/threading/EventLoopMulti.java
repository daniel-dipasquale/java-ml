package com.dipasquale.threading;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.ExceptionHandler;
import com.dipasquale.common.RandomSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

final class EventLoopMulti implements EventLoop {
    private final List<EventLoop> eventLoops;
    private final MultiWaitHandle eventLoopsWaitHandle;
    private final ExceptionHandler eventLoopsShutdownExceptionHandler;
    private final RandomSupport randomSupport;

    EventLoopMulti(final EventLoop.Factory eventLoopFactory, final int count, final DateTimeSupport dateTimeSupport, final RandomSupport randomSupport) {
        List<EventLoop> eventLoops = createEventLoops(eventLoopFactory, count, this);

        this.eventLoops = eventLoops;
        this.eventLoopsWaitHandle = MultiWaitHandle.create(dateTimeSupport, a -> !isEmpty(), eventLoops, EventLoop::awaitUntilEmpty, EventLoop::awaitUntilEmpty);
        this.eventLoopsShutdownExceptionHandler = ExceptionHandler.create(eventLoops, EventLoop::shutdown);
        this.randomSupport = randomSupport;
    }

    private static List<EventLoop> createEventLoops(final EventLoop.Factory eventLoopFactory, final int count, final EventLoopMulti eventLoop) {
        List<EventLoop> eventLoops = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            eventLoops.add(eventLoopFactory.create(eventLoop));
        }

        return eventLoops;
    }

    private EventLoop getNextEventLoop() {
        int index = (int) randomSupport.next(0L, eventLoops.size());

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
        eventLoopsWaitHandle.await();
    }

    @Override
    public boolean awaitUntilEmpty(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return eventLoopsWaitHandle.await(timeout, unit);
    }

    @Override
    public void shutdown() {
        eventLoopsShutdownExceptionHandler.invokeAllAndThrowAsSuppressedIfAny("unable to shutdown the event loops");
    }
}
