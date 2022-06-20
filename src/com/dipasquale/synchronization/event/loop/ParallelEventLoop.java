package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.concurrent.AtomicLazyReference;
import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.common.error.ErrorHandlerController;
import com.dipasquale.synchronization.wait.handle.CountDownWaitHandle;
import com.dipasquale.synchronization.wait.handle.InteractiveWaitHandle;
import com.dipasquale.synchronization.wait.handle.WaitHandleController;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

public final class ParallelEventLoop {
    private static final int DELAY_TIME = 0;
    private final List<EventLoop> eventLoops;
    private final AtomicLazyReference<List<Long>> threadIds;
    private final WaitHandleController eventLoops_whileBusy_waitHandle;
    private final ErrorHandlerController<EventLoop> eventLoops_shutdownHandler;

    private static boolean areEmpty(final List<EventLoop> eventLoops) {
        return eventLoops.stream()
                .allMatch(EventLoop::isEmpty);
    }

    private ParallelEventLoop(final ParallelEventLoopSettings settings, final List<EventLoop> eventLoops) {
        this.eventLoops = eventLoops;
        this.threadIds = new AtomicLazyReference<>(this::captureThreadIds);
        this.eventLoops_whileBusy_waitHandle = new WaitHandleController(eventLoops, settings.getDateTimeSupport(), __ -> !areEmpty(eventLoops));
        this.eventLoops_shutdownHandler = new ErrorHandlerController<>(eventLoops, EventLoop::shutdown);
    }

    private static List<EventLoop> createEventLoops(final ParallelEventLoopSettings settings) {
        List<EventLoop> eventLoops = new ArrayList<>();

        EventLoopParams params = EventLoopParams.builder()
                .executorService(settings.getExecutorService())
                .dateTimeSupport(settings.getDateTimeSupport())
                .errorHandler(settings.getErrorHandler())
                .build();

        for (int i = 0, c = settings.getNumberOfThreads(); i < c; i++) {
            eventLoops.add(new ZeroDelayEventLoop(params, null));
        }

        return eventLoops;
    }

    @Builder
    public ParallelEventLoop(final ParallelEventLoopSettings settings) {
        this(settings, createEventLoops(settings));
    }

    private List<Long> captureThreadIds() {
        List<Long> threadIds = new ArrayList<>();

        for (EventLoop eventLoop : eventLoops) {
            threadIds.addAll(eventLoop.getThreadIds());
        }

        return List.copyOf(threadIds);
    }

    public List<Long> getThreadIds() {
        return threadIds.getReference();
    }

    private <T> InteractiveWaitHandle queue(final IteratorProducerFactory<T> iteratorProducerFactory, final ItemHandler<T> itemHandler, final ErrorHandler errorHandler) {
        int size = eventLoops.size();
        InteractiveWaitHandle interactiveWaitHandle = new CountDownWaitHandle(size);

        for (int i = 0; i < size; i++) {
            EventLoop eventLoop = eventLoops.get(i);
            ItemProducer<T> itemProducer = iteratorProducerFactory.create(i, size);
            EventLoopHandler handler = new ItemProducerEventLoopHandler<>(itemProducer, itemHandler);

            eventLoop.queue(handler, DELAY_TIME, errorHandler, interactiveWaitHandle);
        }

        return interactiveWaitHandle;
    }

    public <T> InteractiveWaitHandle queue(final Iterator<T> iterator, final ItemHandler<T> itemHandler, final ErrorHandler errorHandler) {
        ItemProducer<T> itemProducer = new SharedItemProducer<>(iterator);
        IteratorProducerFactory<T> iteratorProducerFactory = (__, ___) -> itemProducer;

        return queue(iteratorProducerFactory, itemHandler, errorHandler);
    }

    public <T> InteractiveWaitHandle queue(final Iterator<T> iterator, final ItemHandler<T> itemHandler) {
        return queue(iterator, itemHandler, null);
    }

    public <T> InteractiveWaitHandle queue(final List<T> list, final ItemHandler<T> itemHandler, final ErrorHandler errorHandler) {
        IteratorProducerFactory<T> iteratorProducerFactory = (offset, step) -> new IsolatedItemProducer<>(list, offset, step);

        return queue(iteratorProducerFactory, itemHandler, errorHandler);
    }

    public <T> InteractiveWaitHandle queue(final List<T> list, final ItemHandler<T> itemHandler) {
        return queue(list, itemHandler, null);
    }

    private static Iterable<Range> createRanges(final int offset, final int count, final int size) {
        int temporaryCount = count / size;
        int plusOneEndIndex = count % size;

        return IntStream.range(0, size)
                .mapToObj(index -> {
                    int fixedOffset = offset + index;
                    int fixedCount = temporaryCount + (index < plusOneEndIndex ? 1 : 0);

                    return new Range(index, fixedOffset, fixedCount);
                })
                ::iterator;
    }

    public InteractiveWaitHandle queue(final int offset, final int count, final RangeHandler rangeHandler, final ErrorHandler errorHandler) {
        int size = eventLoops.size();
        InteractiveWaitHandle interactiveWaitHandle = new CountDownWaitHandle(size);

        for (Range range : createRanges(offset, count, size)) {
            RangeEventLoopHandler handler = new RangeEventLoopHandler(rangeHandler, range.offset, range.count);

            eventLoops.get(range.index).queue(handler, DELAY_TIME, errorHandler, interactiveWaitHandle);
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

    public <TProxy, TArgument> ParallelExecutionContext<TArgument> createExecutionContext(final int count, final ParallelExecutionProxyFactory<TProxy> proxyFactory, final ParallelExecutionHandler<TProxy, TArgument> proxyHandler) {
        ParallelExecutionContext<TArgument> executionContext = new ParallelExecutionContext<>();
        List<EventLoopHandler> handlers = executionContext.getHandlers();

        for (Range range : createRanges(0, count, eventLoops.size())) {
            TProxy proxy = proxyFactory.create(range.offset, range.count);
            EventLoopHandler handler = () -> proxyHandler.handle(proxy, executionContext.getArgument());

            handlers.add(handler);
        }

        return executionContext;
    }

    private <T> void queue(final ParallelExecutionContext<T> executionContext, final T argument, final ErrorHandler errorHandler, final InteractiveWaitHandle interactiveWaitHandle) {
        executionContext.getLock().lock();

        try {
            executionContext.setArgument(argument);

            for (int i = 0, c = eventLoops.size(); i < c; i++) {
                EventLoop eventLoop = eventLoops.get(i);
                EventLoopHandler handler = executionContext.getHandlers().get(i);

                eventLoop.queue(handler, DELAY_TIME, errorHandler, interactiveWaitHandle);
            }
        } finally {
            executionContext.getLock().unlock();
        }
    }

    public <T> InteractiveWaitHandle queue(final ParallelExecutionContext<T> executionContext, final T argument, final ErrorHandler errorHandler) {
        int size = eventLoops.size();
        int contextSize = executionContext.getHandlers().size();

        if (contextSize != size) {
            String message = String.format("The number of event loops available (%d) does not match the executionContext handlers (%d)", size, contextSize);

            throw new IllegalArgumentException(message);
        }

        InteractiveWaitHandle interactiveWaitHandle = new CountDownWaitHandle(size);

        queue(executionContext, argument, errorHandler, interactiveWaitHandle);

        return interactiveWaitHandle;
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

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Range {
        private final int index;
        private final int offset;
        private final int count;
    }
}
