package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.concurrent.AtomicLazyReference;
import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.common.error.ErrorHandlerController;
import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.synchronization.wait.handle.InteractiveWaitHandle;
import com.dipasquale.synchronization.wait.handle.WaitHandleController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

final class RouterEventLoop implements EventLoop {
    private final List<EventLoop> eventLoops;
    private final AtomicLazyReference<List<Long>> threadIds;
    private final EventLoopSelector selector;
    private final WaitHandleController eventLoops_busy_waitHandle;
    private final ErrorHandlerController<EventLoop> eventLoops_shutdownHandler;

    private static List<EventLoop> createEventLoops(final EventLoopFactory.Proxy factoryProxy, final int size, final RouterEventLoop entryPoint) {
        List<EventLoop> eventLoops = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            EventLoop eventLoop = factoryProxy.create(entryPoint);

            eventLoops.add(eventLoop);
        }

        return eventLoops;
    }

    RouterEventLoop(final EventLoopFactory.Proxy factoryProxy, final int size, final EventLoopSelector selector, final DateTimeSupport dateTimeSupport) {
        List<EventLoop> eventLoops = createEventLoops(factoryProxy, size, this);

        this.eventLoops = eventLoops;
        this.threadIds = new AtomicLazyReference<>(this::captureThreadIds);
        this.selector = selector;
        this.eventLoops_busy_waitHandle = new WaitHandleController(eventLoops, dateTimeSupport, __ -> !isEmpty());
        this.eventLoops_shutdownHandler = new ErrorHandlerController<>(eventLoops, EventLoop::shutdown);
    }

    private List<Long> captureThreadIds() {
        List<Long> threadIds = new ArrayList<>();

        for (EventLoop eventLoop : eventLoops) {
            threadIds.addAll(eventLoop.getThreadIds());
        }

        return List.copyOf(threadIds);
    }

    @Override
    public List<Long> getThreadIds() {
        return threadIds.getReference();
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
        eventLoops_busy_waitHandle.await();
    }

    @Override
    public boolean await(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return eventLoops_busy_waitHandle.await(timeout, unit);
    }

    @Override
    public void clear() {
        eventLoops.forEach(EventLoop::clear);
    }

    @Override
    public void shutdown() {
        eventLoops_shutdownHandler.handleAll("unable to shutdown the event loops");
    }
}
