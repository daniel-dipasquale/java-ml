package com.dipasquale.threading.event.loop;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.error.ErrorLogger;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public interface EventLoop {
    String getName();

    int getConcurrencyLevel();

    void queue(EventLoopHandler handler, long delayTime, ErrorLogger errorLogger, CountDownLatch invokedCountDownLatch);

    default void queue(final EventLoopHandler handler, final long delayTime, final CountDownLatch invokedCountDownLatch) {
        queue(handler, delayTime, null, invokedCountDownLatch);
    }

    default void queue(final EventLoopHandler handler, final long delayTime) {
        queue(handler, delayTime, null, null);
    }

    void queue(IntervalEventLoopHandler handler, long delayTime, ErrorLogger errorLogger, CountDownLatch invokedCountDownLatch);

    default void queue(final IntervalEventLoopHandler handler, final long delayTime, final CountDownLatch invokedCountDownLatch) {
        queue(handler, delayTime, null, invokedCountDownLatch);
    }

    default void queue(final IntervalEventLoopHandler handler, final long delayTime) {
        queue(handler, delayTime, null, null);
    }

    boolean isEmpty();

    void awaitUntilEmpty() throws InterruptedException;

    boolean awaitUntilEmpty(long timeout, TimeUnit unit) throws InterruptedException;

    void shutdown();

    static EventLoop create(final EventLoopSettings settings) {
        ArgumentValidatorSupport.ensureGreaterThanZero(settings.getConcurrencyLevel(), "settings.concurrencyLevel");

        DefaultEventLoopParams params = DefaultEventLoopParams.builder()
                .executorService(settings.getExecutorService())
                .dateTimeSupport(settings.getDateTimeSupport())
                .errorLogger(settings.getErrorLogger())
                .build();

        if (settings.getConcurrencyLevel() == 1) {
            return settings.getFactoryProxy().create(settings.getName(), settings.getEventRecordQueueFactory(), params, null);
        }

        String name = String.format("%s-multi", settings.getName());
        int[] index = new int[1];
        SingleEventLoopFactoryProxy eventLoopFactory = nep -> settings.getFactoryProxy().create(String.format("%s-%d", settings.getName(), ++index[0]), settings.getEventRecordQueueFactory(), params, nep);

        EventLoopSelector eventLoopSelector = Optional.ofNullable(settings.getEventLoopSelector())
                .orElseGet(() -> EventLoopSelector.createRandom(settings.isContended(), settings.getConcurrencyLevel()));

        return new MultiEventLoop(name, eventLoopFactory, eventLoopSelector, settings.getDateTimeSupport());
    }
}
