package com.dipasquale.threading;

import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.ExceptionLogger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

final class EventLoopDefault implements EventLoop {
    private final Queue<Record> eventHandlers;
    private final DateTimeSupport dateTimeSupport;
    private final ReusableCountDownLatch waitUntilEmptyHandle;
    private final SlidingWaitHandle slidingWaitHandle;
    private final ExceptionLogger exceptionLogger;
    private final EventLoop nextLoop;
    private final AtomicBoolean shutdown;
    private boolean isWaitHandleLocked;

    EventLoopDefault(final Queue<Record> eventHandlers, final DateTimeSupport dateTimeSupport, final String name, final ExceptionLogger exceptionLogger, final EventLoop nextLoop, final ExecutorService executorService) {
        EventLoop nextLoopFixed = Optional.ofNullable(nextLoop)
                .orElse(this);

        this.eventHandlers = eventHandlers;
        this.dateTimeSupport = dateTimeSupport;
        this.isWaitHandleLocked = false;
        this.waitUntilEmptyHandle = new ReusableCountDownLatch(0);
        this.slidingWaitHandle = new SlidingWaitHandle(name);
        this.exceptionLogger = exceptionLogger;
        this.nextLoop = nextLoopFixed;
        this.shutdown = new AtomicBoolean(false);
        executorService.submit(this::handleEvent);
    }

    private RecordAudit produceNextEventIfPossible() {
        synchronized (eventHandlers) {
            Record record = eventHandlers.peek();

            if (record == null || dateTimeSupport.now() < record.executionDateTime) {
                return new RecordAudit(record, null);
            }

            return new RecordAudit(record, eventHandlers.poll());
        }
    }

    private long getDelayTime(final Record record) {
        return Math.max(record.executionDateTime - dateTimeSupport.now(), 0L);
    }

    private void ensureToReleaseWaitHandleIfEmpty() {
        if (eventHandlers.isEmpty() && isWaitHandleLocked) {
            isWaitHandleLocked = false;
            waitUntilEmptyHandle.countDown();
        }
    }

    private void handleEvent() {
        while (!shutdown.get()) {
            RecordAudit recordAudit = produceNextEventIfPossible();

            if (recordAudit.polled == null) {
                try {
                    synchronized (eventHandlers) {
                        ensureToReleaseWaitHandleIfEmpty();
                    }

                    if (recordAudit.peeked != null) {
                        slidingWaitHandle.await(getDelayTime(recordAudit.peeked), dateTimeSupport.timeUnit());
                    } else {
                        slidingWaitHandle.await();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                    throw new RuntimeException("thread was interrupted while waiting for the next event in the loop", e);
                }
            } else {
                try {
                    recordAudit.polled.eventHandler.run();
                } catch (Throwable e) {
                    exceptionLogger.log(e);
                } finally {
                    synchronized (eventHandlers) {
                        ensureToReleaseWaitHandleIfEmpty();
                    }
                }
            }
        }
    }

    private void queue(final Record record) {
        synchronized (eventHandlers) {
            eventHandlers.add(record);
            slidingWaitHandle.changeAwait(getDelayTime(eventHandlers.peek()), dateTimeSupport.timeUnit());

            if (!isWaitHandleLocked) {
                isWaitHandleLocked = true;
                waitUntilEmptyHandle.countUp();
            }
        }
    }

    @Override
    public void queue(final Runnable handler, final long delayTime) {
        queue(new Record(handler, dateTimeSupport.now() + delayTime));
    }

    @Override
    public void queue(final EventLoop.Handler handler) {
        queue(new HandlerProxy(handler), handler.getDelayTime());
    }

    @Override
    public boolean isEmpty() {
        synchronized (eventHandlers) {
            return eventHandlers.isEmpty();
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
            synchronized (eventHandlers) {
                eventHandlers.clear();
                ensureToReleaseWaitHandleIfEmpty();
            }
        }
    }

    @FunctionalInterface
    interface FactoryProxy {
        EventLoop create(DateTimeSupport dateTimeSupport, String name, ExceptionLogger exceptionLogger, EventLoop nextLoop, ExecutorService executorService);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    static class Record {
        private final Runnable eventHandler;
        private final long executionDateTime;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class RecordAudit {
        private final Record peeked;
        private final Record polled;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private final class HandlerProxy implements Runnable {
        private final EventLoop.Handler handler;

        @Override
        public void run() {
            handler.handle();

            if (handler.shouldReQueue()) {
                nextLoop.queue(handler);
            }
        }
    }
}
