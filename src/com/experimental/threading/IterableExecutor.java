//package com.experimental.threading;
//
//import com.dipasquale.common.ArgumentValidatorUtils;
//import com.dipasquale.common.DateTimeSupport;
//import com.dipasquale.common.ExceptionLogger;
//import com.dipasquale.common.MultiExceptionHandler;
//import com.dipasquale.data.structure.set.DequeSet;
//import com.dipasquale.data.structure.set.IdentityDequeSet;
//import com.dipasquale.threading.el.EventLoop;
//import com.dipasquale.threading.EventLoopDefaultParams;
//import com.dipasquale.threading.EventLoopNoDelay;
//import com.dipasquale.threading.EventLoopRecordQueueFactory;
//import com.dipasquale.threading.ExclusiveQueueLocked;
//import com.dipasquale.threading.wait.handle.MultiWaitHandle;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//import java.util.stream.Collectors;
//
//public final class IterableExecutor {
//    private static final EventLoopRecordQueueFactory EVENT_RECORDS_FACTORY = q -> new ExclusiveQueueLocked<>(new ReentrantLock(), q);
//    private final Map<String, EventLoopHandlersPair> eventLoopHandlers;
//    private final MultiWaitHandle waitUntilEmptyEventLoopsHandle;
//    private final MultiExceptionHandler shutdownEventLoopsHandler;
//
//    public IterableExecutor(final ExecutorService executorService, final int numberOfThreads, final ExceptionLogger exceptionLogger, final DateTimeSupport dateTimeSupport) {
//        ArgumentValidatorUtils.ensureGreaterThanZero(numberOfThreads, "numberOfThreads");
//
//        List<EventLoop> eventLoops = createEventLoops(dateTimeSupport, exceptionLogger, executorService, numberOfThreads);
//        Map<String, EventLoopHandlersPair> eventLoopHandlers = createEventLoopHandlers(eventLoops);
//
//        this.eventLoopHandlers = eventLoopHandlers;
//        this.waitUntilEmptyEventLoopsHandle = MultiWaitHandle.create(dateTimeSupport, a -> !isEmpty(eventLoops, eventLoopHandlers), eventLoops, EventLoop::awaitUntilEmpty, EventLoop::awaitUntilEmpty);
//        this.shutdownEventLoopsHandler = MultiExceptionHandler.create(eventLoops, EventLoop::shutdown);
//    }
//
//    private static List<EventLoop> createEventLoops(final DateTimeSupport dateTimeSupport, final ExceptionLogger exceptionLogger, final ExecutorService executorService, final int numberOfThreads) {
//        List<EventLoop> eventLoops = new ArrayList<>();
//
//        EventLoopDefaultParams params = EventLoopDefaultParams.builder()
//                .dateTimeSupport(dateTimeSupport)
//                .exceptionLogger(exceptionLogger)
//                .executorService(executorService)
//                .build();
//
//        for (int i = 0; i < numberOfThreads; i++) {
//            EventLoop eventLoop = new EventLoopNoDelay(String.format("eventLoop-%d", i), EVENT_RECORDS_FACTORY, params, null);
//
//            eventLoops.add(eventLoop);
//        }
//
//        return eventLoops;
//    }
//
//    private static boolean isEmpty(final List<EventLoop> eventLoops, final Map<String, EventLoopHandlersPair> eventLoopHandlers) {
//        boolean isEmpty = eventLoopHandlers.values().stream()
//                .allMatch(elh -> {
//                    elh.lock.lock();
//
//                    try {
//                        return elh.handlers.isEmpty();
//                    } finally {
//                        elh.lock.unlock();
//                    }
//                });
//
//        if (!isEmpty) {
//            return false;
//        }
//
//        return eventLoops.stream()
//                .allMatch(EventLoop::isEmpty);
//    }
//
//    private static Map<String, EventLoopHandlersPair> createEventLoopHandlers(final List<EventLoop> eventLoops) {
//        return eventLoops.stream()
//                .collect(Collectors.toMap(EventLoop::getName, EventLoopHandlersPair::new));
//    }
//
//    <T> List<IterableExecutorHandler<T>> push(final IterableExecutorBuilder<T> builder) {
//        int index = 0;
//        List<IterableExecutorHandler<T>> handlers = builder.build(eventLoopHandlers.size());
//
//        for (Map.Entry<String, EventLoopHandlersPair> entry : eventLoopHandlers.entrySet()) {
//            EventLoopHandlersPair eventLoopHandler = entry.getValue();
//
//            eventLoopHandler.lock.lock();
//
//            try {
//                IterableExecutorHandler<T> handlerNew = handlers.get(index++);
//                IterableExecutorHandler<?> handlerCurrent = eventLoopHandler.handlers.getLast();
//
//                if (handlerCurrent != null) {
//                    handlerCurrent.stopProcessing();
//                }
//
//                eventLoopHandler.handlers.addLast(handlerNew);
//                eventLoopHandler.eventLoop.queue(handlerNew, 0L);
//            } finally {
//                eventLoopHandler.lock.unlock();
//            }
//        }
//
//        return handlers;
//    }
//
//    private <T> void suspendOrRemove(final String name, final IterableExecutorHandler<T> handler) {
//        EventLoopHandlersPair eventLoopHandler = eventLoopHandlers.get(name);
//
//        eventLoopHandler.lock.lock();
//
//        try {
//            if (handler != null) {
//                eventLoopHandler.handlers.remove(handler);
//            }
//
//            if (!eventLoopHandler.handlers.isEmpty()) {
//                eventLoopHandler.eventLoop.queue(eventLoopHandler.handlers.getLast(), 0L);
//            }
//        } finally {
//            eventLoopHandler.lock.unlock();
//        }
//    }
//
//    <T> void suspend(final String name) {
//        suspendOrRemove(name, null);
//    }
//
//    <T> void remove(final String name, final IterableExecutorHandler<T> handler) {
//        suspendOrRemove(name, handler);
//    }
//
//    public <T> IterableExecutorBuilder<T> iterate(final Iterable<T> iterable) {
//        return new IterableExecutorBuilderDefault<>(this, iterable);
//    }
//
//    public void awaitUntilDone()
//            throws InterruptedException {
//        waitUntilEmptyEventLoopsHandle.await();
//    }
//
//    public void shutdown() {
//        shutdownEventLoopsHandler.invokeAllAndThrowAsSuppressedIfAny("unable to shutdown the event loops");
//    }
//
//    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
//    private static final class EventLoopHandlersPair {
//        private final EventLoop eventLoop;
//        private final DequeSet<IterableExecutorHandler<?>> handlers = new IdentityDequeSet<>();
//        private final Lock lock = new ReentrantLock();
//    }
//}
