package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.common.error.IterableErrorHandler;
import com.dipasquale.synchronization.wait.handle.CountDownWaitHandle;
import com.dipasquale.synchronization.wait.handle.InteractiveWaitHandle;
import com.dipasquale.synchronization.wait.handle.MultiWaitHandle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public final class BatchingEventLoop {
    private final List<EventLoop> eventLoops;
    private final MultiWaitHandle eventLoops_whileBusy_waitHandle;
    private final IterableErrorHandler<EventLoop> eventLoops_shutdownHandler;

    private BatchingEventLoop(final BatchingEventLoopSettings settings, final List<EventLoop> eventLoops) {
        this.eventLoops = eventLoops;
        this.eventLoops_whileBusy_waitHandle = new MultiWaitHandle(eventLoops, settings.getDateTimeSupport(), __ -> !areEmpty(eventLoops));
        this.eventLoops_shutdownHandler = new IterableErrorHandler<>(eventLoops, EventLoop::shutdown);
    }

    public BatchingEventLoop(final BatchingEventLoopSettings settings) {
        this(settings, createEventLoops(settings));
    }

    private static List<EventLoop> createEventLoops(final BatchingEventLoopSettings settings) {
        List<EventLoop> eventLoops = new ArrayList<>();

        EventLoopParams params = EventLoopParams.builder()
                .executorService(settings.getExecutorService())
                .dateTimeSupport(settings.getDateTimeSupport())
                .errorHandler(settings.getErrorHandler())
                .build();

        for (int i = 0, c = settings.getNumberOfThreads(); i < c; i++) {
            EventLoop eventLoop = new NoDelayEventLoop(String.format("eventLoop-%d", i), params, null);

            eventLoops.add(eventLoop);
        }

        return eventLoops;
    }

    private static boolean areEmpty(final List<EventLoop> eventLoops) {
        return eventLoops.stream()
                .allMatch(EventLoop::isEmpty);
    }

    public int getConcurrencyLevel() {
        return eventLoops.size();
    }

    private <T> InteractiveWaitHandle queue(final IteratorProducerFactory<T> iteratorProducerFactory, final Consumer<T> itemHandler, final ErrorHandler errorHandler) {
        int size = eventLoops.size();
        InteractiveWaitHandle invokedWaitHandle = new CountDownWaitHandle(size);

        for (int i = 0; i < size; i++) {
            IteratorProducer<T> iteratorProducer = iteratorProducerFactory.create(i, size);
            EventLoopHandler handler = new BatchingEventLoopHandler<>(iteratorProducer, itemHandler);

            eventLoops.get(i).queue(handler, 0L, errorHandler, invokedWaitHandle);
        }

        return invokedWaitHandle;
    }

    public <T> InteractiveWaitHandle queue(final Iterator<T> iterator, final Consumer<T> itemHandler, final ErrorHandler errorHandler) {
        IteratorProducer<T> iteratorProducer = new SynchronizedIteratorProducer<>(iterator);
        IteratorProducerFactory<T> iteratorProducerFactory = (__, ___) -> iteratorProducer;

        return queue(iteratorProducerFactory, itemHandler, errorHandler);
    }

    public <T> InteractiveWaitHandle queue(final Iterator<T> iterator, final Consumer<T> itemHandler) {
        return queue(iterator, itemHandler, null);
    }

    public <T> InteractiveWaitHandle queue(final List<T> list, final Consumer<T> itemHandler, final ErrorHandler errorHandler) {
        IteratorProducerFactory<T> iteratorProducerFactory = (offset, step) -> new IsolatedIteratorProducer<>(list, offset, step);

        return queue(iteratorProducerFactory, itemHandler, errorHandler);
    }

    public <T> InteractiveWaitHandle queue(final List<T> list, final Consumer<T> itemHandler) {
        return queue(list, itemHandler, null);
    }

    public void awaitUntilDone()
            throws InterruptedException {
        eventLoops_whileBusy_waitHandle.await();
    }

    public void clear() {
        eventLoops.forEach(EventLoop::clear);
    }

    public void shutdown() {
        eventLoops_shutdownHandler.handleAll("unable to shutdown the event loops");
    }

    @FunctionalInterface
    private interface IteratorProducerFactory<T> {
        IteratorProducer<T> create(int offset, int step);
    }
}
