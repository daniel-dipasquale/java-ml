package com.dipasquale.threading.event.loop;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.ErrorLogger;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public interface EventLoop {
    String getName();

    int getConcurrencyLevel();

    void queue(EventLoopHandler handler, long delayTime, ErrorLogger errorLogger, CountDownLatch countDownLatch);

    default void queue(final EventLoopHandler handler, final long delayTime, final CountDownLatch countDownLatch) {
        queue(handler, delayTime, null, countDownLatch);
    }

    default void queue(final EventLoopHandler handler, final long delayTime) {
        queue(handler, delayTime, null, null);
    }

    void queue(EventLoopIntervalHandler handler, long delayTime, ErrorLogger errorLogger, CountDownLatch countDownLatch);

    default void queue(final EventLoopIntervalHandler handler, final long delayTime, final CountDownLatch countDownLatch) {
        queue(handler, delayTime, null, countDownLatch);
    }

    default void queue(final EventLoopIntervalHandler handler, final long delayTime) {
        queue(handler, delayTime, null, null);
    }

    boolean isEmpty();

    void awaitUntilEmpty() throws InterruptedException;

    boolean awaitUntilEmpty(long timeout, TimeUnit unit) throws InterruptedException;

    void shutdown();

    private static EventLoopSelector getOrCreateEventLoopSelector(final EventLoopSettings settings) {
        return Optional.ofNullable(settings.getSelector())
                .orElseGet(() -> EventLoopSelector.createRandom(settings.isContended(), settings.getConcurrencyLevel()));
    }

    static EventLoop create(final EventLoopSettings settings) {
        ArgumentValidatorSupport.ensureGreaterThanZero(settings.getConcurrencyLevel(), "settings.concurrencyLevel");

        EventLoopDefaultParams params = EventLoopDefaultParams.builder()
                .executorService(settings.getExecutorService())
                .dateTimeSupport(settings.getDateTimeSupport())
                .errorLogger(settings.getErrorLogger())
                .build();

        if (settings.getConcurrencyLevel() == 1) {
            return settings.getFactoryProxy().create(settings.getName(), settings.getEventRecordsFactory(), params, null);
        }

        String name = String.format("%s-multi", settings.getName());
        int[] index = new int[1];
        EventLoopFactory eventLoopFactory = nel -> settings.getFactoryProxy().create(String.format("%s-%d", settings.getName(), ++index[0]), settings.getEventRecordsFactory(), params, nel);
        EventLoopSelector eventLoopSelector = getOrCreateEventLoopSelector(settings);

        return new EventLoopMulti(name, eventLoopFactory, eventLoopSelector, settings.getDateTimeSupport());
    }

    static EventLoopIterable createForIterables(final EventLoopIterableSettings settings) {
        return new EventLoopIterable(settings);
    }
}
