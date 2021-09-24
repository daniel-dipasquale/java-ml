package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.common.error.IterableErrorHandler;
import com.dipasquale.synchronization.wait.handle.CountDownWaitHandle;
import com.dipasquale.synchronization.wait.handle.InteractiveWaitHandle;
import com.dipasquale.synchronization.wait.handle.MultiWaitHandle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public final class IterableEventLoop {
    private final List<EventLoop> eventLoops;
    private final MultiWaitHandle eventLoopsWaitHandleUntilDone;
    private final IterableErrorHandler<EventLoop> eventLoopsShutdownHandler;

    public IterableEventLoop(final IterableEventLoopSettings settings) {
        ArgumentValidatorSupport.ensureGreaterThanZero(settings.getNumberOfThreads(), "settings.numberOfThreads");

        List<EventLoop> eventLoops = createEventLoops(settings);

        this.eventLoops = eventLoops;
        this.eventLoopsWaitHandleUntilDone = MultiWaitHandle.create(eventLoops, EventLoopWaitHandle::new, settings.getDateTimeSupport(), a -> !isEmpty(eventLoops));
        this.eventLoopsShutdownHandler = new IterableErrorHandler<>(eventLoops, EventLoop::shutdown);
    }

    private static List<EventLoop> createEventLoops(final IterableEventLoopSettings settings) {
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

    private static boolean isEmpty(final List<EventLoop> eventLoops) {
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
            EventLoopHandler handler = new IterableEventLoopHandler<>(iteratorProducer, itemHandler);

            eventLoops.get(i).queue(handler, 0L, errorHandler, invokedWaitHandle);
        }

        return invokedWaitHandle;
    }

    public <T> InteractiveWaitHandle queue(final Iterator<T> iterator, final Consumer<T> itemHandler, final ErrorHandler errorHandler) {
        IteratorProducer<T> iteratorProducer = new SynchronizedIteratorProducer<>(iterator);
        IteratorProducerFactory<T> iteratorProducerFactory = (i, c) -> iteratorProducer;

        return queue(iteratorProducerFactory, itemHandler, errorHandler);
    }

    public <T> InteractiveWaitHandle queue(final Iterator<T> iterator, final Consumer<T> itemHandler) {
        return queue(iterator, itemHandler, null);
    }

    public <T> InteractiveWaitHandle queue(final List<T> list, final Consumer<T> itemHandler, final ErrorHandler errorHandler) {
        IteratorProducerFactory<T> iteratorProducerFactory = (i, c) -> new IsolatedIteratorProducer<>(list, i, c);

        return queue(iteratorProducerFactory, itemHandler, errorHandler);
    }

    public <T> InteractiveWaitHandle queue(final List<T> list, final Consumer<T> itemHandler) {
        return queue(list, itemHandler, null);
    }

    public void awaitUntilDone()
            throws InterruptedException {
        eventLoopsWaitHandleUntilDone.await();
    }

    public void shutdown() {
        eventLoopsShutdownHandler.handleAll("unable to shutdown the event loops");
    }

    @FunctionalInterface
    private interface IteratorProducerFactory<T> {
        IteratorProducer<T> create(int offset, int step);
    }
}
