package com.dipasquale.threading;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.ExceptionLogger;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

final class EventLoopDefault implements EventLoop {
    private final ExclusiveQueue<EventLoopRecord> eventRecords;
    private final DateTimeSupport dateTimeSupport;
    private final ReusableCountDownLatch waitUntilEmptyHandle;
    private final SlidingWaitHandle waitWhileEmptyHandle;
    private final ExceptionLogger exceptionLogger;
    private final EventLoop nextEventLoop;
    private final AtomicBoolean shutdown;
    private boolean isWaitHandleLocked;

    EventLoopDefault(final ExclusiveQueue<EventLoopRecord> eventRecords, final EventLoopDefaultParams params, final String name, final EventLoop nextEventLoop) {
        EventLoop nextEventLoopFixed = Optional.ofNullable(nextEventLoop)
                .orElse(this);

        this.eventRecords = eventRecords;
        this.dateTimeSupport = params.getDateTimeSupport();
        this.isWaitHandleLocked = false;
        this.waitUntilEmptyHandle = new ReusableCountDownLatch(0);
        this.waitWhileEmptyHandle = new SlidingWaitHandle(name);
        this.exceptionLogger = params.getExceptionLogger();
        this.nextEventLoop = nextEventLoopFixed;
        this.shutdown = new AtomicBoolean(false);
        params.getExecutorService().submit(this::handleEvent);
    }

    private EventLoopRecordAudit produceNextEventRecordIfPossible() {
        eventRecords.lock();

        try {
            EventLoopRecord eventRecord = eventRecords.peek();

            if (eventRecord == null || dateTimeSupport.now() < eventRecord.getExecutionDateTime()) {
                return new EventLoopRecordAudit(eventRecord, null);
            }

            return new EventLoopRecordAudit(eventRecord, eventRecords.poll());
        } finally {
            eventRecords.unlock();
        }
    }

    private long getDelayTime(final EventLoopRecord eventRecord) {
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
            EventLoopRecordAudit eventRecordAudit = produceNextEventRecordIfPossible();

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

    private void queue(final EventLoopRecord eventRecord) {
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
        queue(new EventLoopRecord(handler, dateTimeSupport.now() + delayTime));
    }

    @Override
    public void queue(final EventLoopHandler handler) {
        queue(new EventLoopHandlerProxy(handler), handler.getDelayTime());
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

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class EventLoopRecordAudit {
        private final EventLoopRecord peeked;
        private final EventLoopRecord polled;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class EventLoopHandlerProxy implements Runnable {
        private final EventLoopHandler handler;

        @Override
        public void run() {
            handler.handle();

            if (handler.shouldReQueue()) {
                nextEventLoop.queue(handler);
            }
        }
    }
}
