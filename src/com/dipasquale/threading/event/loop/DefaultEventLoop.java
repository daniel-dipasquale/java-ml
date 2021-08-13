package com.dipasquale.threading.event.loop;

import com.dipasquale.common.error.ErrorLogger;
import com.dipasquale.common.time.DateTimeSupport;
import com.dipasquale.threading.wait.handle.ReusableCountLatch;
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
    private final ExclusiveQueue<Record> recordQueue;
    private final ExecutorService executorService;
    private boolean started;
    private final DateTimeSupport dateTimeSupport;
    private boolean isRecordQueueUntilEmptyWaitHandleOn;
    private final ReusableCountLatch recordQueueUntilEmptyWaitHandle;
    private final SlidingWaitHandle recordQueueWhileEmptyWaitHandle;
    private final ErrorLogger errorLogger;
    private final EventLoop nextEventLoop;
    private final AtomicBoolean shutdown;

    DefaultEventLoop(final String name, final ExclusiveQueue<Record> recordQueue, final DefaultEventLoopParams params, final EventLoop nextEventLoop) {
        EventLoop nextEventLoopFixed = Optional.ofNullable(nextEventLoop)
                .orElse(this);

        this.name = name;
        this.recordQueue = recordQueue;
        this.executorService = params.getExecutorService();
        this.started = false;
        this.dateTimeSupport = params.getDateTimeSupport();
        this.isRecordQueueUntilEmptyWaitHandleOn = false;
        this.recordQueueUntilEmptyWaitHandle = new ReusableCountLatch(0);
        this.recordQueueWhileEmptyWaitHandle = new SlidingWaitHandle(name);
        this.errorLogger = params.getErrorLogger();
        this.nextEventLoop = nextEventLoopFixed;
        this.shutdown = new AtomicBoolean(true);
    }

    @Override
    public int getConcurrencyLevel() {
        return CONCURRENCY_LEVEL;
    }

    private RecordAudit pollNextRecordIfPossible() {
        recordQueue.lock();

        try {
            Record record = recordQueue.peek();

            if (record == null || dateTimeSupport.now() < record.getExecutionDateTime()) {
                return new RecordAudit(record, null);
            }

            return new RecordAudit(record, recordQueue.poll());
        } finally {
            recordQueue.unlock();
        }
    }

    private long getDelayTime(final Record record) {
        return Math.max(record.getExecutionDateTime() - dateTimeSupport.now(), 0L);
    }

    private void releaseWaitUntilEmptyHandleIfEmpty(final boolean contended) {
        if (contended) {
            recordQueue.lock();
        }

        try {
            if (recordQueue.isEmpty() && isRecordQueueUntilEmptyWaitHandleOn) {
                isRecordQueueUntilEmptyWaitHandleOn = false;
                recordQueueUntilEmptyWaitHandle.countDown();
            }
        } finally {
            if (contended) {
                recordQueue.unlock();
            }
        }
    }

    private void handleEvent() {
        while (!shutdown.get()) {
            RecordAudit recordAudit = pollNextRecordIfPossible();

            if (recordAudit.polled == null) {
                try {
                    releaseWaitUntilEmptyHandleIfEmpty(true);

                    if (recordAudit.peeked != null) {
                        long timeout = getDelayTime(recordAudit.peeked);
                        TimeUnit unit = dateTimeSupport.timeUnit();

                        recordQueueWhileEmptyWaitHandle.await(timeout, unit);
                    } else {
                        recordQueueWhileEmptyWaitHandle.await();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                    throw new RuntimeException("thread was interrupted while waiting for the next event in the loop", e);
                }
            } else {
                try {
                    recordAudit.polled.getHandler().handle(name);
                } catch (Throwable e) {
                    if (errorLogger != null) {
                        errorLogger.log(e);
                    }
                } finally {
                    releaseWaitUntilEmptyHandleIfEmpty(true);
                }
            }
        }
    }

    private void queue(final Record record) {
        recordQueue.lock();

        try {
            recordQueue.push(record);

            if (!started) {
                started = true;
                shutdown.set(false);
                executorService.submit(this::handleEvent);
            } else {
                long timeout = getDelayTime(recordQueue.peek());
                TimeUnit unit = dateTimeSupport.timeUnit();

                recordQueueWhileEmptyWaitHandle.changeTimeout(timeout, unit);
            }

            if (!isRecordQueueUntilEmptyWaitHandleOn) {
                isRecordQueueUntilEmptyWaitHandleOn = true;
                recordQueueUntilEmptyWaitHandle.countUp();
            }
        } finally {
            recordQueue.unlock();
        }
    }

    @Override
    public void queue(final EventLoopHandler handler, final long delayTime, final ErrorLogger errorLogger, final CountDownLatch invokedCountDownLatch) {
        EventLoopHandler handlerFixed = new StandardEventLoopHandler(handler, errorLogger, invokedCountDownLatch);

        queue(new Record(handlerFixed, dateTimeSupport.now() + delayTime));
    }

    @Override
    public void queue(final IntervalEventLoopHandler handler, final long delayTime, final ErrorLogger errorLogger, final CountDownLatch invokedCountDownLatch) {
        EventLoopHandler handlerFixed = new StandardIntervalEventLoopHandler(handler, delayTime, errorLogger, invokedCountDownLatch, nextEventLoop);

        queue(new Record(handlerFixed, dateTimeSupport.now() + delayTime));
    }

    @Override
    public boolean isEmpty() {
        recordQueue.lock();

        try {
            return !isRecordQueueUntilEmptyWaitHandleOn;
        } finally {
            recordQueue.unlock();
        }
    }

    @Override
    public void awaitUntilEmpty()
            throws InterruptedException {
        recordQueueUntilEmptyWaitHandle.await();
    }

    @Override
    public boolean awaitUntilEmpty(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        return recordQueueUntilEmptyWaitHandle.await(timeout, unit);
    }

    @Override
    public void shutdown() {
        if (!shutdown.getAndSet(true)) {
            recordQueue.lock();

            try {
                recordQueue.clear();
                releaseWaitUntilEmptyHandleIfEmpty(false);
                recordQueueWhileEmptyWaitHandle.release();
            } finally {
                recordQueue.unlock();
            }
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class RecordAudit {
        private final Record peeked;
        private final Record polled;
    }
}
