package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.synchronization.InterruptedRuntimeException;
import com.dipasquale.synchronization.wait.handle.InteractiveWaitHandle;
import com.dipasquale.synchronization.wait.handle.TogglingWaitHandle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
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
    private final EventLoopId id;
    private final Queue<EventLoopRecord> queue;
    private final ExecutorService executorService;
    private final DateTimeSupport dateTimeSupport;
    private final Lock addToOrRemoveFromQueueLock;
    private final Lock handleQueuedEventsLock;
    private final Condition handleQueuedEventsLock_emptyQueue_condition;
    private final TogglingWaitHandle handlingQueuedEvent_waitHandle;
    private final ErrorHandler errorHandler;
    private final EventLoop entryPoint;
    private boolean started;
    private final AtomicBoolean shutdown;

    private ExplicitDelayEventLoop(final EventLoopId id, final ExplicitDelayEventLoopParams params, final Lock handleQueuedEventsLock, final EventLoop entryPoint) {
        this.id = id;
        this.queue = params.getEventLoopRecords();
        this.executorService = params.getExecutorService();
        this.dateTimeSupport = params.getDateTimeSupport();
        this.addToOrRemoveFromQueueLock = new ReentrantLock();
        this.handleQueuedEventsLock = handleQueuedEventsLock;
        this.handleQueuedEventsLock_emptyQueue_condition = handleQueuedEventsLock.newCondition();
        this.handlingQueuedEvent_waitHandle = new TogglingWaitHandle();
        this.errorHandler = params.getErrorHandler();
        this.entryPoint = Objects.requireNonNullElse(entryPoint, this);
        this.started = false;
        this.shutdown = new AtomicBoolean(true);
    }

    ExplicitDelayEventLoop(final EventLoopId id, final ExplicitDelayEventLoopParams params, final EventLoop entryPoint) {
        this(id, params, new ReentrantLock(), entryPoint);
    }

    @Override
    public int getConcurrencyLevel() {
        return CONCURRENCY_LEVEL;
    }

    private EventLoopRecordCandidate retrieveNextRecordCandidate() {
        EventLoopRecord record = queue.peek();

        if (record == null || dateTimeSupport.now() < record.getExecutionDateTime()) {
            return new EventLoopRecordCandidate(record, null);
        }

        return new EventLoopRecordCandidate(record, queue.poll());
    }

    private void notifyNotBusyIfApplicable() {
        if (queue.isEmpty()) {
            handlingQueuedEvent_waitHandle.countDown();
        }
    }

    private long getDelayTime(final EventLoopRecord record) {
        return Math.max(record.getExecutionDateTime() - dateTimeSupport.now(), 0L);
    }

    private void handleQueuedEvents() {
        handleQueuedEventsLock.lock();

        try {
            while (!shutdown.get()) {
                EventLoopRecordCandidate audit = retrieveNextRecordCandidate();

                if (audit.polled == null) {
                    try {
                        notifyNotBusyIfApplicable();

                        if (audit.peeked != null) {
                            long timeout = getDelayTime(audit.peeked);

                            handleQueuedEventsLock_emptyQueue_condition.await(timeout, dateTimeSupport.timeUnit());
                        } else {
                            handleQueuedEventsLock_emptyQueue_condition.await();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();

                        throw new InterruptedRuntimeException("thread was interrupted while waiting for the next event in the loop", e);
                    }
                } else {
                    try {
                        audit.polled.getHandler().handle(id);
                    } catch (Throwable e) {
                        if (errorHandler != null) {
                            errorHandler.handle(e);
                        }
                    } finally {
                        notifyNotBusyIfApplicable();
                    }
                }
            }
        } finally {
            handleQueuedEventsLock.unlock();
        }
    }

    private void queue(final EventLoopRecord record) {
        addToOrRemoveFromQueueLock.lock();

        try {
            handleQueuedEventsLock.lock();

            try {
                queue.add(record);

                if (!started) {
                    started = true;
                    shutdown.set(false);
                    executorService.submit(this::handleQueuedEvents);
                } else {
                    handleQueuedEventsLock_emptyQueue_condition.signal();
                }

                handlingQueuedEvent_waitHandle.countUp();
            } finally {
                handleQueuedEventsLock.unlock();
            }
        } finally {
            addToOrRemoveFromQueueLock.unlock();
        }
    }

    @Override
    public void queue(final EventLoopHandler handler, final long delayTime, final ErrorHandler errorHandler, final InteractiveWaitHandle invokedWaitHandle) {
        EventLoopHandler fixedHandler = new StandardEventLoopHandler(handler, errorHandler, invokedWaitHandle);
        long executionDateTime = dateTimeSupport.now() + delayTime;

        queue(new EventLoopRecord(fixedHandler, executionDateTime));
    }

    @Override
    public void queue(final IntervalEventLoopHandler handler, final long delayTime, final ErrorHandler errorHandler, final InteractiveWaitHandle invokedWaitHandle) {
        EventLoopHandler fixedHandler = new StandardIntervalEventLoopHandler(handler, delayTime, errorHandler, invokedWaitHandle, entryPoint);
        long executionDateTime = dateTimeSupport.now() + delayTime;

        queue(new EventLoopRecord(fixedHandler, executionDateTime));
    }

    @Override
    public boolean isEmpty() {
        addToOrRemoveFromQueueLock.lock();

        try {
            handleQueuedEventsLock.lock();

            try {
                return queue.isEmpty();
            } finally {
                handleQueuedEventsLock.unlock();
            }
        } finally {
            addToOrRemoveFromQueueLock.unlock();
        }
    }

    @Override
    public void await()
            throws InterruptedException {
        handlingQueuedEvent_waitHandle.await();
    }

    @Override
    public boolean await(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return handlingQueuedEvent_waitHandle.await(timeout, unit);
    }

    @Override
    public void clear() {
        handleQueuedEventsLock.lock();

        try {
            queue.clear();
            handlingQueuedEvent_waitHandle.countDown();
        } finally {
            handleQueuedEventsLock.unlock();
        }
    }

    @Override
    public void shutdown() {
        if (!shutdown.getAndSet(true)) {
            handleQueuedEventsLock.lock();

            try {
                queue.clear();
                handlingQueuedEvent_waitHandle.countDown();
                handleQueuedEventsLock_emptyQueue_condition.signal();
            } finally {
                handleQueuedEventsLock.unlock();
            }
        }
    }

    @Override
    public String toString() {
        return id.toString();
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class EventLoopRecordCandidate {
        private final EventLoopRecord peeked;
        private final EventLoopRecord polled;
    }
}
