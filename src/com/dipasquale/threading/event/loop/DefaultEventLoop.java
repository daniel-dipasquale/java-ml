package com.dipasquale.threading.event.loop;

import com.dipasquale.common.error.ErrorLogger;
import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.threading.wait.handle.ReusableLatch;
import com.dipasquale.threading.wait.handle.SlidingWaitHandle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

final class DefaultEventLoop implements EventLoop {
    private static final int CONCURRENCY_LEVEL = 1;
    @Getter
    private final String name;
    private final ExclusiveQueue<EventRecord> eventRecordQueue;
    private final ExecutorService executorService;
    private boolean started;
    private final DateTimeSupport dateTimeSupport;
    private boolean isEventRecordQueueWaitHandleUntilEmptyOn;
    private final ReusableLatch eventRecordQueueWaitHandleUntilEmpty;
    private final SlidingWaitHandle eventRecordQueueWaitHandleWhileEmpty;
    private final ErrorLogger errorLogger;
    private final EventLoop nextEntryPoint;
    private final AtomicBoolean shutdown;

    DefaultEventLoop(final String name, final ExclusiveQueue<EventRecord> eventRecordQueue, final DefaultEventLoopParams params, final EventLoop nextEntryPoint) {
        EventLoop nextEntryPointFixed = Optional.ofNullable(nextEntryPoint)
                .orElse(this);

        this.name = name;
        this.eventRecordQueue = eventRecordQueue;
        this.executorService = params.getExecutorService();
        this.started = false;
        this.dateTimeSupport = params.getDateTimeSupport();
        this.isEventRecordQueueWaitHandleUntilEmptyOn = false;
        this.eventRecordQueueWaitHandleUntilEmpty = new ReusableLatch(0);
        this.eventRecordQueueWaitHandleWhileEmpty = new SlidingWaitHandle(name);
        this.errorLogger = params.getErrorLogger();
        this.nextEntryPoint = nextEntryPointFixed;
        this.shutdown = new AtomicBoolean(true);
    }

    @Override
    public int getConcurrencyLevel() {
        return CONCURRENCY_LEVEL;
    }

    private EventRecordAudit pollNextEventIfAvailable() {
        eventRecordQueue.lock();

        try {
            EventRecord eventRecord = eventRecordQueue.peek();

            if (eventRecord == null || dateTimeSupport.now() < eventRecord.getExecutionDateTime()) {
                return new EventRecordAudit(eventRecord, null);
            }

            return new EventRecordAudit(eventRecord, eventRecordQueue.poll());
        } finally {
            eventRecordQueue.unlock();
        }
    }

    private long getDelayTime(final EventRecord eventRecord) {
        return Math.max(eventRecord.getExecutionDateTime() - dateTimeSupport.now(), 0L);
    }

    private void releaseQueueUntilEmptyWaitHandleIfEmpty(final boolean contended) {
        if (contended) {
            eventRecordQueue.lock();
        }

        try {
            if (eventRecordQueue.isEmpty() && isEventRecordQueueWaitHandleUntilEmptyOn) {
                isEventRecordQueueWaitHandleUntilEmptyOn = false;
                eventRecordQueueWaitHandleUntilEmpty.countDown();
            }
        } finally {
            if (contended) {
                eventRecordQueue.unlock();
            }
        }
    }

    private void handleNextEvent() {
        while (!shutdown.get()) {
            EventRecordAudit eventRecordAudit = pollNextEventIfAvailable();

            if (eventRecordAudit.polled == null) {
                try {
                    releaseQueueUntilEmptyWaitHandleIfEmpty(true);

                    if (eventRecordAudit.peeked != null) {
                        long timeout = getDelayTime(eventRecordAudit.peeked);
                        TimeUnit unit = dateTimeSupport.timeUnit();

                        eventRecordQueueWaitHandleWhileEmpty.await(timeout, unit);
                    } else {
                        eventRecordQueueWaitHandleWhileEmpty.await();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                    throw new RuntimeException("thread was interrupted while waiting for the next event in the loop", e);
                }
            } else {
                try {
                    eventRecordAudit.polled.getHandler().handle(name);
                } catch (Throwable e) {
                    if (errorLogger != null) {
                        errorLogger.log(e);
                    }
                } finally {
                    releaseQueueUntilEmptyWaitHandleIfEmpty(true);
                }
            }
        }
    }

    private void queue(final EventRecord eventRecord) {
        eventRecordQueue.lock();

        try {
            eventRecordQueue.push(eventRecord);

            if (!started) {
                started = true;
                shutdown.set(false);
                executorService.submit(this::handleNextEvent);
            } else {
                long timeout = getDelayTime(eventRecordQueue.peek());
                TimeUnit unit = dateTimeSupport.timeUnit();

                eventRecordQueueWaitHandleWhileEmpty.changeTimeout(timeout, unit);
            }

            if (!isEventRecordQueueWaitHandleUntilEmptyOn) {
                isEventRecordQueueWaitHandleUntilEmptyOn = true;
                eventRecordQueueWaitHandleUntilEmpty.countUp();
            }
        } finally {
            eventRecordQueue.unlock();
        }
    }

    @Override
    public void queue(final EventLoopHandler handler, final long delayTime, final ErrorLogger errorLogger, final CountDownLatch invokedCountDownLatch) {
        EventLoopHandler handlerFixed = new StandardEventLoopHandler(handler, errorLogger, invokedCountDownLatch);

        queue(new EventRecord(handlerFixed, dateTimeSupport.now() + delayTime));
    }

    @Override
    public void queue(final IntervalEventLoopHandler handler, final long delayTime, final ErrorLogger errorLogger, final CountDownLatch invokedCountDownLatch) {
        EventLoopHandler handlerFixed = new StandardIntervalEventLoopHandler(handler, delayTime, errorLogger, invokedCountDownLatch, nextEntryPoint);

        queue(new EventRecord(handlerFixed, dateTimeSupport.now() + delayTime));
    }

    @Override
    public boolean isEmpty() {
        eventRecordQueue.lock();

        try {
            return !isEventRecordQueueWaitHandleUntilEmptyOn;
        } finally {
            eventRecordQueue.unlock();
        }
    }

    @Override
    public void awaitUntilEmpty()
            throws InterruptedException {
        eventRecordQueueWaitHandleUntilEmpty.await();
    }

    @Override
    public boolean awaitUntilEmpty(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return eventRecordQueueWaitHandleUntilEmpty.await(timeout, unit);
    }

    @Override
    public void shutdown() {
        if (!shutdown.getAndSet(true)) {
            eventRecordQueue.lock();

            try {
                eventRecordQueue.clear();
                releaseQueueUntilEmptyWaitHandleIfEmpty(false);
                eventRecordQueueWaitHandleWhileEmpty.release();
            } finally {
                eventRecordQueue.unlock();
            }
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class EventRecordAudit {
        private final EventRecord peeked;
        private final EventRecord polled;
    }
}
