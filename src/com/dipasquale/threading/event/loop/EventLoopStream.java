package com.dipasquale.threading.event.loop;

import com.dipasquale.common.ArgumentValidatorUtils;
import com.dipasquale.common.ExceptionLogger;
import com.dipasquale.common.MultiExceptionHandler;
import com.dipasquale.threading.wait.handle.MultiWaitHandle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public final class EventLoopStream {
    private static final EventLoopRecordQueueFactory EVENT_RECORDS_FACTORY = q -> new ExclusiveQueueLocked<>(new ReentrantLock(), q);
    private final List<EventLoop> eventLoops;
    private final MultiWaitHandle waitUntilDoneHandler;
    private final MultiExceptionHandler<EventLoop> shutdownHandler;

    EventLoopStream(final EventLoopStreamSettings settings) {
        ArgumentValidatorUtils.ensureGreaterThanZero(settings.getNumberOfThreads(), "settings.numberOfThreads");

        List<EventLoop> eventLoops = createEventLoops(settings);

        this.eventLoops = eventLoops;
        this.waitUntilDoneHandler = new MultiWaitHandle(settings.getDateTimeSupport(), a -> !isEmpty(eventLoops), EventLoopWaitHandle.translate(eventLoops));
        this.shutdownHandler = new MultiExceptionHandler<>(eventLoops, EventLoop::shutdown);
    }

    private static List<EventLoop> createEventLoops(final EventLoopStreamSettings settings) {
        List<EventLoop> eventLoops = new ArrayList<>();

        EventLoopDefaultParams params = EventLoopDefaultParams.builder()
                .dateTimeSupport(settings.getDateTimeSupport())
                .exceptionLogger(settings.getExceptionLogger())
                .executorService(settings.getExecutorService())
                .build();

        for (int i = 0, c = settings.getNumberOfThreads(); i < c; i++) {
            EventLoop eventLoop = new EventLoopNoDelay(String.format("eventLoop-%d", i), EVENT_RECORDS_FACTORY, params, null);

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

    public <T> CountDownLatch queue(final Stream<T> stream, final EventLoopStreamAction<T> action, final ExceptionLogger exceptionLogger) {
        CountDownLatch countDownLatch = new CountDownLatch(eventLoops.size());
        EventLoopStreamHandler<T> handler = new EventLoopStreamHandler<>(stream.iterator(), action);

        for (EventLoop eventLoop : eventLoops) {
            eventLoop.queue(handler, 0L, exceptionLogger, countDownLatch);
        }

        return countDownLatch;
    }

    public <T> CountDownLatch queue(final Stream<T> stream, final EventLoopStreamAction<T> action) {
        return queue(stream, action, null);
    }

    public void awaitUntilDone()
            throws InterruptedException {
        waitUntilDoneHandler.await();
    }

    public void shutdown() {
        shutdownHandler.invokeAllAndReportAsSuppressed("unable to shutdown the event loops");
    }
}
