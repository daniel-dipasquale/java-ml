package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.concurrent.AtomicLazyReference;
import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.synchronization.InterruptedRuntimeException;
import com.dipasquale.synchronization.wait.handle.InteractiveWaitHandle;
import com.dipasquale.synchronization.wait.handle.TogglingWaitHandle;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

final class ExplicitDelayEventLoop implements EventLoop {
    private static final long ZERO_DELAY = 0L;
    private final AtomicLazyReference<List<Long>> threadIds;
    private final Queue<EventRecord> eventRecords;
    private final ExecutorService executorService;
    private final DateTimeSupport dateTimeSupport;
    private final Lock producerLock;
    private final Lock consumerLock;
    private final Condition consumerLock_empty_condition;
    private final TogglingWaitHandle consuming_waitHandle;
    private final ErrorHandler errorHandler;
    private final EventLoop entryPoint;
    private boolean started;
    private final AtomicBoolean shutdown;

    private ExplicitDelayEventLoop(final ExplicitDelayEventLoopParams params, final Lock consumerLock, final EventLoop entryPoint) {
        this.threadIds = new AtomicLazyReference<>(this::captureThreadIds);
        this.eventRecords = params.getEventRecords();
        this.executorService = params.getExecutorService();
        this.dateTimeSupport = params.getDateTimeSupport();
        this.producerLock = new ReentrantLock();
        this.consumerLock = consumerLock;
        this.consumerLock_empty_condition = consumerLock.newCondition();
        this.consuming_waitHandle = new TogglingWaitHandle();
        this.errorHandler = params.getErrorHandler();
        this.entryPoint = Objects.requireNonNullElse(entryPoint, this);
        this.started = false;
        this.shutdown = new AtomicBoolean(true);
    }

    ExplicitDelayEventLoop(final ExplicitDelayEventLoopParams params, final EventLoop entryPoint) {
        this(params, new ReentrantLock(), entryPoint);
    }

    private CandidateEventRecord retrieveCandidateNextEventRecord() {
        EventRecord eventRecord = eventRecords.peek();

        if (eventRecord == null || dateTimeSupport.now() < eventRecord.getExecutionDateTime()) {
            return new CandidateEventRecord(eventRecord, null);
        }

        return new CandidateEventRecord(eventRecord, eventRecords.poll());
    }

    private void notifyNotBusyIfApplicable() {
        if (eventRecords.isEmpty()) {
            consuming_waitHandle.countDown();
        }
    }

    private long getDelayTime(final EventRecord record) {
        return Math.max(record.getExecutionDateTime() - dateTimeSupport.now(), ZERO_DELAY);
    }

    private void handleQueuedEvents() {
        consumerLock.lock();

        try {
            while (!shutdown.get()) {
                CandidateEventRecord candidateEventRecord = retrieveCandidateNextEventRecord();

                if (candidateEventRecord.polled == null) {
                    try {
                        notifyNotBusyIfApplicable();

                        if (candidateEventRecord.peeked != null) {
                            long timeout = getDelayTime(candidateEventRecord.peeked);

                            consumerLock_empty_condition.await(timeout, dateTimeSupport.timeUnit());
                        } else {
                            consumerLock_empty_condition.await();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();

                        throw new InterruptedRuntimeException("thread was interrupted while waiting for the next event in the loop", e);
                    }
                } else {
                    try {
                        candidateEventRecord.polled.getHandler().handle();
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
            consumerLock.unlock();
        }
    }

    private void queue(final EventRecord eventRecord) {
        producerLock.lock();

        try {
            consumerLock.lock();

            try {
                eventRecords.add(eventRecord);

                if (!started) {
                    started = true;
                    shutdown.set(false);
                    executorService.submit(this::handleQueuedEvents);
                } else {
                    consumerLock_empty_condition.signal();
                }

                consuming_waitHandle.countUp();
            } finally {
                consumerLock.unlock();
            }
        } finally {
            producerLock.unlock();
        }
    }

    private List<Long> captureThreadIds() {
        ThreadIdCatcher threadIdCatcher = new ThreadIdCatcher();

        queue(new ThreadIdCatcherEventLoopHandler(threadIdCatcher), ZERO_DELAY);

        try {
            threadIdCatcher.countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new InterruptedRuntimeException("unable to capture the thread id", e);
        }

        return List.of(threadIdCatcher.value);
    }

    @Override
    public List<Long> getThreadIds() {
        return threadIds.getReference();
    }

    @Override
    public void queue(final EventLoopHandler handler, final long delayTime, final ErrorHandler errorHandler, final InteractiveWaitHandle invokedWaitHandle) {
        EventLoopHandler fixedHandler = new StandardEventLoopHandler(handler, errorHandler, invokedWaitHandle);
        long executionDateTime = dateTimeSupport.now() + delayTime;

        queue(new EventRecord(fixedHandler, executionDateTime));
    }

    @Override
    public void queue(final IntervalEventLoopHandler handler, final long delayTime, final ErrorHandler errorHandler, final InteractiveWaitHandle invokedWaitHandle) {
        EventLoopHandler fixedHandler = new StandardIntervalEventLoopHandler(handler, delayTime, errorHandler, invokedWaitHandle, entryPoint);
        long executionDateTime = dateTimeSupport.now() + delayTime;

        queue(new EventRecord(fixedHandler, executionDateTime));
    }

    @Override
    public boolean isEmpty() {
        producerLock.lock();

        try {
            consumerLock.lock();

            try {
                return eventRecords.isEmpty();
            } finally {
                consumerLock.unlock();
            }
        } finally {
            producerLock.unlock();
        }
    }

    @Override
    public void await()
            throws InterruptedException {
        consuming_waitHandle.await();
    }

    @Override
    public boolean await(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return consuming_waitHandle.await(timeout, unit);
    }

    @Override
    public void clear() {
        consumerLock.lock();

        try {
            eventRecords.clear();
            consuming_waitHandle.countDown();
        } finally {
            consumerLock.unlock();
        }
    }

    @Override
    public void shutdown() {
        if (!shutdown.getAndSet(true)) {
            consumerLock.lock();

            try {
                eventRecords.clear();
                consuming_waitHandle.countDown();
                consumerLock_empty_condition.signal();
            } finally {
                consumerLock.unlock();
            }
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ThreadIdCatcher {
        private Long value = null;
        private final CountDownLatch countDownLatch = new CountDownLatch(1);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class ThreadIdCatcherEventLoopHandler implements EventLoopHandler {
        private final ThreadIdCatcher threadIdCatcher;

        @Override
        public void handle() {
            threadIdCatcher.value = Thread.currentThread().getId();
            threadIdCatcher.countDownLatch.countDown();
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class CandidateEventRecord {
        private final EventRecord peeked;
        private final EventRecord polled;
    }
}
