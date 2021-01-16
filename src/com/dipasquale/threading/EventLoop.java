package com.dipasquale.threading;

import com.dipasquale.common.ArgumentValidator;
import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.ExceptionLogger;
import com.dipasquale.common.RandomSupport;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public interface EventLoop {
    void queue(Runnable handler, long delayTime);

    void queue(EventLoop.Handler handler);

    boolean isEmpty();

    void awaitUntilEmpty()
            throws InterruptedException;

    boolean awaitUntilEmpty(long timeout, TimeUnit unit)
            throws InterruptedException;

    void shutdown();

    private static EventLoop create(final EventLoopDefault.FactoryProxy eventLoopFactoryProxy, final DateTimeSupport dateTimeSupport, final RandomSupport randomSupport, final String name, final int count, final ExceptionLogger exceptionLogger, final ExecutorService executorService) {
        ArgumentValidator.getInstance().ensureGreaterThanZero(count, "count");

        if (count == 1) {
            return eventLoopFactoryProxy.create(dateTimeSupport, name, exceptionLogger, null, executorService);
        }

        int[] index = new int[1];
        EventLoop.Factory eventLoopFactory = nl -> eventLoopFactoryProxy.create(dateTimeSupport, String.format("%s-%d", name, ++index[0]), exceptionLogger, nl, executorService);

        return new EventLoopMulti(eventLoopFactory, count, dateTimeSupport, randomSupport);
    }

    static EventLoop createPriority(final DateTimeSupport dateTimeSupport, final RandomSupport randomSupport, final String name, final int count, final ExceptionLogger exceptionLogger, final ExecutorService executorService) {
        return create(EventLoopPriority::new, dateTimeSupport, randomSupport, name, count, exceptionLogger, executorService);
    }

    static EventLoop createPriority(final DateTimeSupport dateTimeSupport, final String name, final int count, final ExceptionLogger exceptionLogger, final ExecutorService executorService) {
        return createPriority(dateTimeSupport, RandomSupport.createConcurrent(), name, count, exceptionLogger, executorService);
    }

    static EventLoop createPriority(final DateTimeSupport dateTimeSupport, final int count, final ExceptionLogger exceptionLogger, final ExecutorService executorService) {
        return createPriority(dateTimeSupport, EventLoopPriority.class.getSimpleName(), count, exceptionLogger, executorService);
    }

    static EventLoop createFifoAsap(final DateTimeSupport dateTimeSupport, final RandomSupport randomSupport, final String name, final int count, final ExceptionLogger exceptionLogger, final ExecutorService executorService) {
        return create(EventLoopFifoAsap::new, dateTimeSupport, randomSupport, name, count, exceptionLogger, executorService);
    }

    static EventLoop createFifoAsap(final DateTimeSupport dateTimeSupport, final String name, final int count, final ExceptionLogger exceptionLogger, final ExecutorService executorService) {
        return createFifoAsap(dateTimeSupport, RandomSupport.createConcurrent(), name, count, exceptionLogger, executorService);
    }

    static EventLoop createFifoAsap(final DateTimeSupport dateTimeSupport, final int count, final ExceptionLogger exceptionLogger, final ExecutorService executorService) {
        return createFifoAsap(dateTimeSupport, EventLoopFifoAsap.class.getSimpleName(), count, exceptionLogger, executorService);
    }

    interface Handler {
        boolean shouldReQueue();

        long getDelayTime();

        void handle();
    }

    @FunctionalInterface
    interface Factory {
        EventLoop create(EventLoop nextLoop);
    }
}
