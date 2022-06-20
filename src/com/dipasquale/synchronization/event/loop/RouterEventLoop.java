package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.common.error.IterableErrorHandler;
import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.synchronization.wait.handle.InteractiveWaitHandle;
import com.dipasquale.synchronization.wait.handle.MultiWaitHandle;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

final class RouterEventLoop implements EventLoop {
    @Getter
    private final EventLoopId id;
    private final List<EventLoop> eventLoops;
    private final EventLoopSelector selector;
    private final MultiWaitHandle eventLoops_handlingEvents_waitHandle;
    private final IterableErrorHandler<EventLoop> eventLoops_shutdownHandler;

    private static List<EventLoop> createEventLoops(final EventLoopFactory.Proxy eventLoopFactoryProxy, final int count, final RouterEventLoop entryPoint) {
        List<EventLoop> eventLoops = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            EventLoop eventLoop = eventLoopFactoryProxy.create(entryPoint);

            eventLoops.add(eventLoop);
        }

        return Collections.unmodifiableList(eventLoops);
    }

    RouterEventLoop(final EventLoopId id, final EventLoopFactory.Proxy eventLoopFactoryProxy, final int count, final EventLoopSelector selector, final DateTimeSupport dateTimeSupport) {
        List<EventLoop> eventLoops = createEventLoops(eventLoopFactoryProxy, count, this);

        this.id = id;
        this.eventLoops = eventLoops;
        this.selector = selector;
        this.eventLoops_handlingEvents_waitHandle = new MultiWaitHandle(eventLoops, dateTimeSupport, __ -> !isEmpty());
        this.eventLoops_shutdownHandler = new IterableErrorHandler<>(eventLoops, EventLoop::shutdown);
    }

    @Override
    public int getConcurrencyLevel() {
        return eventLoops.size();
    }

    private EventLoop getNextEventLoop() {
        int index = selector.nextIndex(eventLoops);

        return eventLoops.get(index);
    }

    @Override
    public void queue(final EventLoopHandler handler, final long delayTime, final ErrorHandler errorHandler, final InteractiveWaitHandle invokedWaitHandle) {
        getNextEventLoop().queue(handler, delayTime, errorHandler, invokedWaitHandle);
    }

    @Override
    public void queue(final IntervalEventLoopHandler handler, final long delayTime, final ErrorHandler errorHandler, final InteractiveWaitHandle invokedWaitHandle) {
        getNextEventLoop().queue(handler, delayTime, errorHandler, invokedWaitHandle);
    }

    @Override
    public boolean isEmpty() {
        return eventLoops.stream()
                .allMatch(EventLoop::isEmpty);
    }

    @Override
    public void await()
            throws InterruptedException {
        eventLoops_handlingEvents_waitHandle.await();
    }

    @Override
    public boolean await(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return eventLoops_handlingEvents_waitHandle.await(timeout, unit);
    }

    @Override
    public void clear() {
        eventLoops.forEach(EventLoop::clear);
    }

    @Override
    public void shutdown() {
        eventLoops_shutdownHandler.handleAll("unable to shutdown the event loops");
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
