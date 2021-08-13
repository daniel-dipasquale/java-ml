/*
 * java-ml
 * (c) 2021 daniel-dipasquale
 * released under the MIT license
 */

package com.dipasquale.threading.event.loop;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.error.ErrorLogger;
import com.dipasquale.common.error.IterableErrorHandler;
import com.dipasquale.threading.wait.handle.MultiWaitHandle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
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
                .eventRecordQueueFactory(Constants.EVENT_RECORD_QUEUE_FACTORY)
                .executorService(settings.getExecutorService())
                .dateTimeSupport(settings.getDateTimeSupport())
                .errorLogger(settings.getErrorLogger())
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

    private <T> CountDownLatch queue(final IteratorProducer<T> iteratorProducer, final Consumer<T> action, final ErrorLogger errorLogger) {
        EventLoopHandler handler = new IterableEventLoopHandler<>(iteratorProducer, action);
        CountDownLatch invokedCountDownLatch = new CountDownLatch(eventLoops.size());

        for (EventLoop eventLoop : eventLoops) {
            eventLoop.queue(handler, 0L, errorLogger, invokedCountDownLatch);
        }

        return invokedCountDownLatch;
    }

    public <T> CountDownLatch queue(final Iterator<T> iterator, final Consumer<T> action, final ErrorLogger errorLogger) {
        IteratorProducer<T> iteratorProducer = IteratorProducer.createSynchronized(iterator);

        return queue(iteratorProducer, action, errorLogger);
    }

    public <T> CountDownLatch queue(final Iterator<T> iterator, final Consumer<T> action) {
        return queue(iterator, action, null);
    }

    public <T> CountDownLatch queue(final List<T> list, final Consumer<T> action, final ErrorLogger errorLogger) {
        IteratorProducer<T> iteratorProducer = IteratorProducer.createConcurrent(list);

        return queue(iteratorProducer, action, errorLogger);
    }

    public <T> CountDownLatch queue(final List<T> list, final Consumer<T> action) {
        return queue(list, action, null);
    }

    public void awaitUntilDone()
            throws InterruptedException {
        eventLoopsWaitHandleUntilDone.await();
    }

    public void shutdown() {
        eventLoopsShutdownHandler.handleAll("unable to shutdown the event loops");
    }
}
