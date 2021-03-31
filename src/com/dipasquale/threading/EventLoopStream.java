package com.dipasquale.threading;

import com.dipasquale.common.ArgumentValidatorUtils;
import com.dipasquale.common.MultiExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public final class EventLoopStream {
    private static final EventLoopRecordQueueFactory EVENT_RECORDS_FACTORY = q -> new ExclusiveQueueLocked<>(new ReentrantLock(), q);
    private final List<EventLoop> eventLoops;
    private final MultiWaitHandle waitUntilEmptyEventLoopsHandle;
    private final MultiExceptionHandler shutdownEventLoopsHandler;

    EventLoopStream(final EventLoopStreamSettings settings) {
        ArgumentValidatorUtils.ensureGreaterThanZero(settings.getNumberOfThreads(), "settings.numberOfThreads");

        List<EventLoop> eventLoops = createEventLoops(settings);

        this.eventLoops = eventLoops;
        this.waitUntilEmptyEventLoopsHandle = MultiWaitHandle.create(settings.getDateTimeSupport(), a -> !isEmpty(eventLoops), eventLoops, EventLoop::awaitUntilEmpty, EventLoop::awaitUntilEmpty);
        this.shutdownEventLoopsHandler = MultiExceptionHandler.create(eventLoops, EventLoop::shutdown);
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

    public <T> void queue(final Stream<T> stream, final EventLoopStreamAction<T> action) {
        EventLoopStreamHandler<T> handler = new EventLoopStreamHandler<>(stream.iterator(), action);

        for (EventLoop eventLoop : eventLoops) {
            eventLoop.queue(handler, 0L);
        }
    }

    public void awaitUntilDone()
            throws InterruptedException {
        waitUntilEmptyEventLoopsHandle.await();
    }

    public void shutdown() {
        shutdownEventLoopsHandler.invokeAllAndThrowAsSuppressedIfAny("unable to shutdown the event loops");
    }
}
