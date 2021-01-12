package com.dipasquale.threading;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.ExceptionSupport;
import com.dipasquale.common.RandomSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

final class EventLoopMulti implements EventLoop {
    private static final ExceptionSupport EXCEPTION_SUPPORT = ExceptionSupport.getInstance();
    private final List<EventLoop> eventLoops;
    private final RandomSupport randomSupport;
    private final DateTimeSupport dateTimeSupport;

    EventLoopMulti(final EventLoop.Factory eventLoopFactory, final int count, final RandomSupport randomSupport, final DateTimeSupport dateTimeSupport) {
        this.eventLoops = createEventLoops(eventLoopFactory, count, this);
        this.randomSupport = randomSupport;
        this.dateTimeSupport = dateTimeSupport;
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
        while (!isEmpty()) {
            for (EventLoop eventLoop : eventLoops) {
                eventLoop.awaitUntilEmpty();
            }
        }
    }

    @Override
    public boolean awaitUntilEmpty(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        long offsetDateTime = dateTimeSupport.now();
        boolean acquired = true;
        long timeoutRemaining = (long) DateTimeSupport.getUnit(unit).getConverterTo(dateTimeSupport.unit()).convert((double) timeout);

        while (!isEmpty()) {
            for (int i = 0, c = eventLoops.size(); i < c && acquired && timeoutRemaining > 0L; i++) {
                EventLoop eventLoop = eventLoops.get(i);

                acquired = eventLoop.awaitUntilEmpty(timeoutRemaining, dateTimeSupport.timeUnit());

                long currentDateTime = dateTimeSupport.now();

                timeoutRemaining -= currentDateTime - offsetDateTime;
                offsetDateTime = currentDateTime;
            }
        }

        return acquired && timeoutRemaining > 0L;
    }

    @Override
    public void shutdown() {
        EXCEPTION_SUPPORT.invokeAllAndThrowAsSuppressedIfAny(eventLoops, EventLoop::shutdown, "unable to shutdown the event loops");
    }
}
