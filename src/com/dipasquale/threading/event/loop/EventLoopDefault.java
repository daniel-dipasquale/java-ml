package com.dipasquale.threading.event.loop;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.ExceptionLogger;
import com.dipasquale.threading.wait.handle.ReusableCountDownLatch;
import com.dipasquale.threading.wait.handle.SlidingWaitHandle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

final class EventLoopDefault implements EventLoop {
    private static final int CONCURRENCY_LEVEL = 1;
    @Getter
    private final String name;
    private final ExclusiveQueue<EventLoopRecord> eventRecords;
    private final ExecutorService executorService;
    private boolean started;
    private final DateTimeSupport dateTimeSupport;
    private boolean isWaitUntilEmptyHandleLocked;
    private final ReusableCountDownLatch waitUntilEmptyHandle;
    private final SlidingWaitHandle waitWhileEmptyHandle;
    private final ExceptionLogger exceptionLogger;
    private final EventLoop nextEventLoop;
    private final AtomicBoolean shutdown;

    EventLoopDefault(final String name, final ExclusiveQueue<EventLoopRecord> eventRecords, final EventLoopDefaultParams params, final EventLoop nextEventLoop) {
        EventLoop nextEventLoopFixed = Optional.ofNullable(nextEventLoop)
                .orElse(this);

        this.name = name;
        this.eventRecords = eventRecords;
        this.executorService = params.getExecutorService();
        this.started = false;
        this.dateTimeSupport = params.getDateTimeSupport();
        this.isWaitUntilEmptyHandleLocked = false;
        this.waitUntilEmptyHandle = new ReusableCountDownLatch(0);
        this.waitWhileEmptyHandle = new SlidingWaitHandle(name);
        this.exceptionLogger = params.getExceptionLogger();
        this.nextEventLoop = nextEventLoopFixed;
        this.shutdown = new AtomicBoolean(true);
    }

    @Override
    public int getConcurrencyLevel() {
        return CONCURRENCY_LEVEL;
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

    private void releaseWaitUntilEmptyHandleIfEmpty() {
        if (eventRecords.isEmpty() && isWaitUntilEmptyHandleLocked) {
            isWaitUntilEmptyHandleLocked = false;
            waitUntilEmptyHandle.countDown();
        }
    }

    private void releaseWaitUntilEmptyHandleIfEmptyButThreadSafe() {
        eventRecords.lock();

        try {
            releaseWaitUntilEmptyHandleIfEmpty();
        } finally {
            eventRecords.unlock();
        }
    }

    private void handleEvent() {
        while (!shutdown.get()) {
            EventLoopRecordAudit eventRecordAudit = produceNextEventRecordIfPossible();

            if (eventRecordAudit.polled == null) {
                try {
                    releaseWaitUntilEmptyHandleIfEmptyButThreadSafe();

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
                    eventRecordAudit.polled.getHandler().handle(name);
                } catch (Throwable e) {
                    if (exceptionLogger != null) {
                        exceptionLogger.log(e);
                    }
                } finally {
                    releaseWaitUntilEmptyHandleIfEmptyButThreadSafe();
                }
            }
        }
    }

    private void queue(final EventLoopRecord eventRecord) {
        eventRecords.lock();

        try {
            eventRecords.push(eventRecord);

            if (!started) {
                started = true;
                shutdown.set(false);
                executorService.submit(this::handleEvent);
            } else {
                waitWhileEmptyHandle.changeTimeout(getDelayTime(eventRecords.peek()), dateTimeSupport.timeUnit());
            }

            if (!isWaitUntilEmptyHandleLocked) {
                isWaitUntilEmptyHandleLocked = true;
                waitUntilEmptyHandle.countUp();
            }
        } finally {
            eventRecords.unlock();
        }
    }

    @Override
    public void queue(final EventLoopHandler handler, final long delayTime, final ExceptionLogger exceptionLogger, final CountDownLatch countDownLatch) {
        EventLoopHandler handlerFixed = new EventLoopHandlerProxy(handler, exceptionLogger, countDownLatch);
        EventLoopRecord eventRecord = new EventLoopRecord(handlerFixed, dateTimeSupport.now() + delayTime);

        queue(eventRecord);
    }

    @Override
    public void queue(final EventLoopIntervalHandler handler, final long delayTime, final ExceptionLogger exceptionLogger, final CountDownLatch countDownLatch) {
        EventLoopHandler handlerFixed = new EventLoopIntervalHandlerProxy(handler, delayTime, exceptionLogger, countDownLatch, nextEventLoop);
        EventLoopRecord eventRecord = new EventLoopRecord(handlerFixed, dateTimeSupport.now() + delayTime);

        queue(eventRecord);
    }

    @Override
    public boolean isEmpty() {
        eventRecords.lock();

        try {
            return !isWaitUntilEmptyHandleLocked;
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
                releaseWaitUntilEmptyHandleIfEmpty();
                waitWhileEmptyHandle.release();
            } finally {
                eventRecords.unlock();
            }
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    private static final class EventLoopRecordAudit {
        private final EventLoopRecord peeked;
        private final EventLoopRecord polled;
    }
}
