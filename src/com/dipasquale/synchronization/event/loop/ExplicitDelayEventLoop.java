package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.synchronization.InterruptedRuntimeException;
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
    private final Lock lowerPriorityLock;
    private final Lock higherPriorityLock;
    private final Condition whileNoEventAvailableCondition;
    private final TogglingWaitHandle untilEmptyWaitHandle;
    private final ErrorHandler errorHandler;
    private final EventLoop nextEntryPoint;
    private boolean started;
    private final AtomicBoolean shutdown;

    private ExplicitDelayEventLoop(final String name, final ExplicitDelayEventLoopParams params, final Lock lock, final EventLoop nextEntryPoint) {
        this.name = name;
        this.eventRecords = params.getEventRecords();
        this.executorService = params.getExecutorService();
        this.dateTimeSupport = params.getDateTimeSupport();
        this.lowerPriorityLock = new ReentrantLock();
        this.higherPriorityLock = lock;
        this.whileNoEventAvailableCondition = lock.newCondition();
        this.untilEmptyWaitHandle = new TogglingWaitHandle();
        this.errorHandler = params.getErrorHandler();
        this.nextEntryPoint = ensureNotNull(nextEntryPoint, this);
        this.started = false;
        this.shutdown = new AtomicBoolean(true);
    }

    ExplicitDelayEventLoop(final String name, final ExplicitDelayEventLoopParams params, final EventLoop nextEntryPoint) {
        this(name, params, new ReentrantLock(), nextEntryPoint);
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
        higherPriorityLock.lock();

        try {
            while (!shutdown.get()) {
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

                        throw new InterruptedRuntimeException("thread was interrupted while waiting for the next event in the loop", e);
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
            }
        } finally {
            higherPriorityLock.unlock();
        }
    }

    private void queue(final EventRecord eventRecord) {
        lowerPriorityLock.lock();

        try {
            higherPriorityLock.lock();

            try {
                eventRecords.add(eventRecord);

                if (!started) {
                    started = true;
                    shutdown.set(false);
                    executorService.submit(this::handleNextEvent);
                } else {
                    whileNoEventAvailableCondition.signal();
                }

                untilEmptyWaitHandle.countUp();
            } finally {
                higherPriorityLock.unlock();
            }
        } finally {
            lowerPriorityLock.unlock();
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
        lowerPriorityLock.lock();

        try {
            higherPriorityLock.lock();

            try {
                return eventRecords.isEmpty();
            } finally {
                higherPriorityLock.unlock();
            }
        } finally {
            lowerPriorityLock.unlock();
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
    public void clear() {
        lowerPriorityLock.lock();

        try {
            higherPriorityLock.lock();

            try {
                eventRecords.clear();
                untilEmptyWaitHandle.countDown();
            } finally {
                higherPriorityLock.unlock();
            }
        } finally {
            lowerPriorityLock.unlock();
        }
    }

    @Override
    public void shutdown() {
        if (!shutdown.getAndSet(true)) {
            higherPriorityLock.lock();

            try {
                eventRecords.clear();
                untilEmptyWaitHandle.countDown();
                whileNoEventAvailableCondition.signal();
            } finally {
                higherPriorityLock.unlock();
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
