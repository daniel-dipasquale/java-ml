package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.common.error.IterableErrorHandler;
import com.dipasquale.synchronization.wait.handle.CountDownWaitHandle;
import com.dipasquale.synchronization.wait.handle.InteractiveWaitHandle;
import com.dipasquale.synchronization.wait.handle.MultiWaitHandle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ParallelEventLoop {
    private final List<EventLoop> eventLoops;
    private final MultiWaitHandle eventLoops_whileBusy_waitHandle;
    private final IterableErrorHandler<EventLoop> eventLoops_shutdownHandler;

    private static boolean areEmpty(final List<EventLoop> eventLoops) {
        return eventLoops.stream()
                .allMatch(EventLoop::isEmpty);
    }

    private ParallelEventLoop(final ParallelEventLoopSettings settings, final List<EventLoop> eventLoops) {
        this.eventLoops = eventLoops;
        this.eventLoops_whileBusy_waitHandle = new MultiWaitHandle(eventLoops, settings.getDateTimeSupport(), __ -> !areEmpty(eventLoops));
        this.eventLoops_shutdownHandler = new IterableErrorHandler<>(eventLoops, EventLoop::shutdown);
    }

    private static List<EventLoop> createEventLoops(final ParallelEventLoopSettings settings) {
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

    public ParallelEventLoop(final ParallelEventLoopSettings settings) {
        this(settings, createEventLoops(settings));
    }

    public int getConcurrencyLevel() {
        return eventLoops.size();
    }

    private <T> InteractiveWaitHandle queue(final IteratorProducerFactory<T> iteratorProducerFactory, final ItemHandler<T> itemHandler, final ErrorHandler errorHandler) {
        int size = eventLoops.size();
        InteractiveWaitHandle interactiveWaitHandle = new CountDownWaitHandle(size);

        for (int i = 0; i < size; i++) {
            ItemProducer<T> itemProducer = iteratorProducerFactory.create(i, size);
            EventLoopHandler handler = new ItemProducerEventLoopHandler<>(itemProducer, itemHandler);

            eventLoops.get(i).queue(handler, 0L, errorHandler, interactiveWaitHandle);
        }

        return interactiveWaitHandle;
    }

    public <T> InteractiveWaitHandle queue(final Iterator<T> iterator, final ItemHandler<T> itemHandler, final ErrorHandler errorHandler) {
        ItemProducer<T> itemProducer = new ContendedItemProducer<>(iterator);
        IteratorProducerFactory<T> iteratorProducerFactory = (__, ___) -> itemProducer;

        return queue(iteratorProducerFactory, itemHandler, errorHandler);
    }

    public <T> InteractiveWaitHandle queue(final Iterator<T> iterator, final ItemHandler<T> itemHandler) {
        return queue(iterator, itemHandler, null);
    }

    public <T> InteractiveWaitHandle queue(final List<T> list, final ItemHandler<T> itemHandler, final ErrorHandler errorHandler) {
        IteratorProducerFactory<T> iteratorProducerFactory = (offset, step) -> new ConfinedItemProducer<>(list, offset, step);

        return queue(iteratorProducerFactory, itemHandler, errorHandler);
    }

    public <T> InteractiveWaitHandle queue(final List<T> list, final ItemHandler<T> itemHandler) {
        return queue(list, itemHandler, null);
    }

    public InteractiveWaitHandle queue(final int offset, final int count, final RangeHandler rangeHandler, final ErrorHandler errorHandler) {
        int size = eventLoops.size();
        int fixedCount = count / size;
        int plusOneEndIndex = count % size;
        InteractiveWaitHandle interactiveWaitHandle = new CountDownWaitHandle(size);

        for (int i = 0; i < size; i++) {
            int actualOffset = offset + i;
            int actualCount = fixedCount + (i < plusOneEndIndex ? 1 : 0);
            EventLoopHandler handler = new RangeEventLoopHandler(rangeHandler, actualOffset, actualCount);

            eventLoops.get(i).queue(handler, 0L, errorHandler, interactiveWaitHandle);
        }

        return interactiveWaitHandle;
    }

    public InteractiveWaitHandle queue(final int count, final RangeHandler rangeHandler, final ErrorHandler errorHandler) {
        return queue(0, count, rangeHandler, errorHandler);
    }

    public InteractiveWaitHandle queue(final int count, final RangeHandler rangeHandler) {
        return queue(count, rangeHandler, null);
    }

    public InteractiveWaitHandle queue(final int offset, final int count, final RangeHandler rangeHandler) {
        return queue(offset, count, rangeHandler, null);
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
        ItemProducer<T> create(int offset, int step);
    }
}
