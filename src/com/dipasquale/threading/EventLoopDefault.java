package com.dipasquale.threading;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.ExceptionLogger;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

final class EventLoopDefault implements EventLoop {
    private final ExclusiveQueue<EventLoop.Record> eventRecords;
    private final DateTimeSupport dateTimeSupport;
    private final ReusableCountDownLatch waitUntilEmptyHandle;
    private final SlidingWaitHandle waitWhileEmptyHandle;
    private final ExceptionLogger exceptionLogger;
    private final EventLoop nextEventLoop;
    private final AtomicBoolean shutdown;
    private boolean isWaitHandleLocked;

    EventLoopDefault(final ExclusiveQueue<EventLoop.Record> eventRecords, final DateTimeSupport dateTimeSupport, final String name, final ExceptionLogger exceptionLogger, final EventLoop nextEventLoop, final ExecutorService executorService) {
        EventLoop nextEventLoopFixed = Optional.ofNullable(nextEventLoop)
                .orElse(this);

        this.eventRecords = eventRecords;
        this.dateTimeSupport = dateTimeSupport;
        this.isWaitHandleLocked = false;
        this.waitUntilEmptyHandle = new ReusableCountDownLatch(0);
        this.waitWhileEmptyHandle = new SlidingWaitHandle(name);
        this.exceptionLogger = exceptionLogger;
        this.nextEventLoop = nextEventLoopFixed;
        this.shutdown = new AtomicBoolean(false);
        executorService.submit(this::handleEvent);
    }

    private EventRecordAudit produceNextEventRecordIfPossible() {
        eventRecords.lock();

        try {
            EventLoop.Record eventRecord = eventRecords.peek();

            if (eventRecord == null || dateTimeSupport.now() < eventRecord.getExecutionDateTime()) {
                return new EventRecordAudit(eventRecord, null);
            }

            return new EventRecordAudit(eventRecord, eventRecords.poll());
        } finally {
            eventRecords.unlock();
        }
    }

    private long getDelayTime(final EventLoop.Record eventRecord) {
        return Math.max(eventRecord.getExecutionDateTime() - dateTimeSupport.now(), 0L);
    }

    private void releaseWaitHandleIfEmpty() {
        if (eventRecords.isEmpty() && isWaitHandleLocked) {
            isWaitHandleLocked = false;
            waitUntilEmptyHandle.countDown();
        }
    }

    private void releaseWaitHandleConcurrentlyIfEmpty() {
        eventRecords.lock();

        try {
            releaseWaitHandleIfEmpty();
        } finally {
            eventRecords.unlock();
        }
    }

    private void handleEvent() {
        while (!shutdown.get()) {
            EventRecordAudit eventRecordAudit = produceNextEventRecordIfPossible();

            if (eventRecordAudit.polled == null) {
                try {
                    releaseWaitHandleConcurrentlyIfEmpty();

                    if (eventRecordAudit.peeked != null) {
                        waitWhileEmptyHandle.await(getDelayTime(eventRecordAudit.peeked), dateTimeSupport.timeUnit());
                    } else {
                        waitWhileEmptyHandle.await();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                    throw new RuntimeException("thread was interrupted while waiting for the next event in the loop", e);
                }
            } else {
                try {
                    eventRecordAudit.polled.getHandler().run();
                } catch (Throwable e) {
                    exceptionLogger.log(e);
                } finally {
                    releaseWaitHandleConcurrentlyIfEmpty();
                }
            }
        }
    }

    private void queue(final EventLoop.Record eventRecord) {
        eventRecords.lock();

        try {
            eventRecords.push(eventRecord);
            waitWhileEmptyHandle.changeAwait(getDelayTime(eventRecords.peek()), dateTimeSupport.timeUnit());

            if (!isWaitHandleLocked) {
                isWaitHandleLocked = true;
                waitUntilEmptyHandle.countUp();
            }
        } finally {
            eventRecords.unlock();
        }
    }

    @Override
    public void queue(final Runnable handler, final long delayTime) {
        queue(new EventLoop.Record(handler, dateTimeSupport.now() + delayTime));
    }

    @Override
    public void queue(final EventLoop.Handler handler) {
        queue(new EventHandlerProxy(handler), handler.getDelayTime());
    }

    @Override
    public boolean isEmpty() {
        eventRecords.lock();

        try {
            return eventRecords.isEmpty();
        } finally {
            eventRecords.unlock();
        }
    }

    @Override
    public void awaitUntilEmpty()
            throws InterruptedException {
        waitUntilEmptyHandle.await();
    }

    @Override
    public boolean awaitUntilEmpty(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return waitUntilEmptyHandle.await(timeout, unit);
    }

    @Override
    public void shutdown() {
        if (!shutdown.getAndSet(true)) {
            eventRecords.lock();

            try {
                eventRecords.clear();
                releaseWaitHandleIfEmpty();
            } finally {
                eventRecords.unlock();
            }
        }
    }

    @FunctionalInterface
    interface FactoryProxy {
        EventLoop create(DateTimeSupport dateTimeSupport, String name, ExceptionLogger exceptionLogger, EventLoop nextEventLoop, ExecutorService executorService);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class EventRecordAudit {
        private final EventLoop.Record peeked;
        private final EventLoop.Record polled;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class EventHandlerProxy implements Runnable {
        private final EventLoop.Handler handler;

        @Override
        public void run() {
            handler.handle();

            if (handler.shouldReQueue()) {
                nextEventLoop.queue(handler);
            }
        }
    }
}
