package com.dipasquale.threading;

import com.dipasquale.common.ArgumentValidatorUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface EventLoop {
    String getName();

    void queue(EventLoopHandler handler, long delayTime);

    void queue(EventLoopQueueableHandler handler);

    boolean isEmpty();

    void awaitUntilEmpty() throws InterruptedException;

    boolean awaitUntilEmpty(long timeout, TimeUnit unit) throws InterruptedException;

    void shutdown();

    private static EventLoopSelector getOrCreateEventLoopSelector(final EventLoopSettings settings) {
        return Optional.ofNullable(settings.getSelector())
                .orElseGet(() -> EventLoopSelector.createRandom(settings.isContended(), settings.getCount()));
    }

    static EventLoop create(final EventLoopSettings settings) {
        ArgumentValidatorUtils.ensureGreaterThanZero(settings.getCount(), "settings.count");

        EventLoopDefaultParams params = EventLoopDefaultParams.builder()
                .dateTimeSupport(settings.getDateTimeSupport())
                .exceptionLogger(settings.getExceptionLogger())
                .executorService(settings.getExecutorService())
                .build();

        if (settings.getCount() == 1) {
            return settings.getFactoryProxy().create(settings.getName(), settings.getEventRecordsFactory(), params, null);
        }

        String name = String.format("%s-multi", settings.getName());
        int[] index = new int[1];
        EventLoopFactory eventLoopFactory = nel -> settings.getFactoryProxy().create(String.format("%s-%d", settings.getName(), ++index[0]), settings.getEventRecordsFactory(), params, nel);
        EventLoopSelector eventLoopSelector = getOrCreateEventLoopSelector(settings);

        return new EventLoopMulti(name, eventLoopFactory, eventLoopSelector, settings.getDateTimeSupport());
    }

    static EventLoopStream createStream(final EventLoopStreamSettings settings) {
        return new EventLoopStream(settings);
    }
}
