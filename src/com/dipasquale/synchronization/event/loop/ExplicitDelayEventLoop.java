package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.synchronization.wait.handle.InteractiveWaitHandle;
import com.dipasquale.synchronization.wait.handle.TogglingWaitHandle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class ExplicitDelayEventLoop implements EventLoop {
    private static final int CONCURRENCY_LEVEL = 1;
    @Getter
    private final String name;
    private final Queue<EventRecord> eventRecords;
    private final ExecutorService executorService;
    private final DateTimeSupport dateTimeSupport;
    private final Lock allLock;
    private final Lock singleLock;
    private final Condition whileNoEventAvailableCondition;
    private final TogglingWaitHandle untilEmptyWaitHandle;
    private final ErrorHandler errorHandler;
    private final EventLoop nextEntryPoint;
    private boolean started;
    private final AtomicBoolean shutdown;

    ExplicitDelayEventLoop(final String name, final ExplicitDelayEventLoopParams params, final EventLoop nextEntryPoint) {
        Lock singleLock = new ReentrantLock();

        this.name = name;
        this.eventRecords = params.getEventRecords();
        this.executorService = params.getExecutorService();
        this.dateTimeSupport = params.getDateTimeSupport();
        this.allLock = new ReentrantLock();
        this.singleLock = singleLock;
        this.whileNoEventAvailableCondition = singleLock.newCondition();
        this.untilEmptyWaitHandle = new TogglingWaitHandle();
        this.errorHandler = params.getErrorHandler();
        this.nextEntryPoint = ensureNotNull(nextEntryPoint, this);
        this.started = false;
        this.shutdown = new AtomicBoolean(true);
    }

    private static <T> T ensureNotNull(final T first, final T second) {
        if (first != null) {
            return first;
        }

        return second;
    }

    @Override
    public int getConcurrencyLevel() {
        return CONCURRENCY_LEVEL;
    }

    private EventRecordAudit pollNextEventIfAvailable() {
        EventRecord eventRecord = eventRecords.peek();

        if (eventRecord == null || dateTimeSupport.now() < eventRecord.getExecutionDateTime()) {
            return new EventRecordAudit(eventRecord, null);
        }

        return new EventRecordAudit(eventRecord, eventRecords.poll());
    }

    private void releaseUntilEmptyWaitHandleIfEmpty() {
        if (eventRecords.isEmpty()) {
            untilEmptyWaitHandle.countDown();
        }
    }

    private long getDelayTime(final EventRecord eventRecord) {
        return Math.max(eventRecord.getExecutionDateTime() - dateTimeSupport.now(), 0L);
    }

    private void handleNextEvent() {
        while (!shutdown.get()) {
            singleLock.lock();

            try {
                EventRecordAudit eventRecordAudit = pollNextEventIfAvailable();

                if (eventRecordAudit.polled == null) {
                    try {
                        releaseUntilEmptyWaitHandleIfEmpty();

                        if (eventRecordAudit.peeked != null) {
                            long timeout = getDelayTime(eventRecordAudit.peeked);
                            TimeUnit unit = dateTimeSupport.timeUnit();

                            whileNoEventAvailableCondition.await(timeout, unit);
                        } else {
                            whileNoEventAvailableCondition.await();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();

                        throw new RuntimeException("thread was interrupted while waiting for the next event in the loop", e);
                    }
                } else {
                    try {
                        eventRecordAudit.polled.getHandler().handle(name);
                    } catch (Throwable e) {
                        if (errorHandler != null) {
                            errorHandler.handle(e);
                        }
                    } finally {
                        releaseUntilEmptyWaitHandleIfEmpty();
                    }
                }
            } finally {
                singleLock.unlock();
            }
        }
    }

    private void queue(final EventRecord eventRecord) {
        allLock.lock();
        singleLock.lock();

        try {
            eventRecords.add(eventRecord);

            if (!started) {
                started = true;
                shutdown.set(false);
                executorService.submit(this::handleNextEvent);
            } else {
                whileNoEventAvailableCondition.signalAll();
            }

            untilEmptyWaitHandle.countUp();
        } finally {
            singleLock.unlock();
            allLock.unlock();
        }
    }

    @Override
    public void queue(final EventLoopHandler handler, final long delayTime, final ErrorHandler errorHandler, final InteractiveWaitHandle invokedWaitHandle) {
        EventLoopHandler handlerFixed = new StandardEventLoopHandler(handler, errorHandler, invokedWaitHandle);

        queue(new EventRecord(handlerFixed, dateTimeSupport.now() + delayTime));
    }

    @Override
    public void queue(final IntervalEventLoopHandler handler, final long delayTime, final ErrorHandler errorHandler, final InteractiveWaitHandle invokedWaitHandle) {
        EventLoopHandler handlerFixed = new StandardIntervalEventLoopHandler(handler, delayTime, errorHandler, invokedWaitHandle, nextEntryPoint);

        queue(new EventRecord(handlerFixed, dateTimeSupport.now() + delayTime));
    }

    @Override
    public boolean isEmpty() {
        allLock.lock();
        singleLock.lock();

        try {
            return eventRecords.isEmpty();
        } finally {
            singleLock.unlock();
            allLock.unlock();
        }
    }

    @Override
    public void awaitUntilEmpty()
            throws InterruptedException {
        untilEmptyWaitHandle.await();
    }

    @Override
    public boolean awaitUntilEmpty(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return untilEmptyWaitHandle.await(timeout, unit);
    }

    @Override
    public void shutdown() {
        if (!shutdown.getAndSet(true)) {
            allLock.lock();
            singleLock.lock();

            try {
                eventRecords.clear();
                untilEmptyWaitHandle.countDown();
                whileNoEventAvailableCondition.signalAll();
            } finally {
                singleLock.unlock();
                allLock.unlock();
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
