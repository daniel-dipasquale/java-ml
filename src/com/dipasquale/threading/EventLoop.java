package com.dipasquale.threading;

import com.dipasquale.common.ArgumentValidator;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface EventLoop {
    void queue(Runnable handler, long delayTime);

    void queue(EventLoopHandler handler);

    boolean isEmpty();

    void awaitUntilEmpty() throws InterruptedException;

    boolean awaitUntilEmpty(long timeout, TimeUnit unit) throws InterruptedException;

    void shutdown();

    private static EventLoopSelector getOrCreateEventLoopSelector(final EventLoopSettings settings) {
        return Optional.ofNullable(settings.getSelector())
                .orElseGet(() -> EventLoopSelector.createRandom(settings.isContended(), settings.getCount()));
    }

    static EventLoop create(final EventLoopSettings settings) {
        ArgumentValidator.getInstance().ensureGreaterThanZero(settings.getCount(), "count");

        EventLoopDefaultParams params = EventLoopDefaultParams.builder()
                .dateTimeSupport(settings.getDateTimeSupport())
                .exceptionLogger(settings.getExceptionLogger())
                .executorService(settings.getExecutorService())
                .build();

        if (settings.getCount() == 1) {
            return settings.getFactoryProxy().create(params, settings.getEventRecordsFactory(), settings.getName(), null);
        }

        int[] index = new int[1];
        EventLoopFactory eventLoopFactory = nel -> settings.getFactoryProxy().create(params, settings.getEventRecordsFactory(), String.format("%s-%d", settings.getName(), ++index[0]), nel);
        EventLoopSelector eventLoopSelector = getOrCreateEventLoopSelector(settings);

        return new EventLoopMulti(eventLoopFactory, eventLoopSelector, settings.getDateTimeSupport());
    }
}
