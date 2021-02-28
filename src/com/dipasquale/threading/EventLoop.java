package com.dipasquale.threading;

import com.dipasquale.common.ArgumentValidator;
import com.dipasquale.common.DateTimeSupport;
import com.dipasquale.common.ExceptionLogger;
import com.dipasquale.common.RandomSupport;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public interface EventLoop {
    void queue(Runnable handler, long delayTime);

    void queue(EventLoop.Handler handler);

    boolean isEmpty();

    void awaitUntilEmpty()
            throws InterruptedException;

    boolean awaitUntilEmpty(long timeout, TimeUnit unit)
            throws InterruptedException;

    void shutdown();

    private static Selector getOrCreateSelector(final Settings settings) {
        return Optional.ofNullable(settings.selector)
                .orElseGet(() -> {
                    RandomSupport randomSupport = settings.contended ? RandomSupport.createConcurrent() : RandomSupport.create();

                    return new Selector() {
                        @Override
                        public int next() {
                            return (int) randomSupport.next(0L, settings.count);
                        }

                        @Override
                        public int size() {
                            return settings.count;
                        }
                    };
                });
    }

    static EventLoop create(final Settings settings) {
        ArgumentValidator.getInstance().ensureGreaterThanZero(settings.count, "count");

        EventLoopDefault.FactoryProxy eventLoopFactoryProxy = Settings.EVENT_LOOP_FACTORIES.get(settings.type);

        EventLoopDefault.Params params = EventLoopDefault.Params.builder()
                .dateTimeSupport(settings.dateTimeSupport)
                .exceptionLogger(settings.exceptionLogger)
                .executorService(settings.executorService)
                .build();

        EventLoopDefault.EventRecordsFactory eventRecordsFactory = Settings.EVENT_RECORDS_FACTORIES.get(settings.contended);

        if (settings.count == 1) {
            return eventLoopFactoryProxy.create(params, eventRecordsFactory, settings.name, null);
        }

        int[] index = new int[1];
        EventLoop.Factory eventLoopFactory = nel -> eventLoopFactoryProxy.create(params, eventRecordsFactory, String.format("%s-%d", settings.name, ++index[0]), nel);
        Selector eventLoopSelector = getOrCreateSelector(settings);

        return new EventLoopMulti(eventLoopFactory, eventLoopSelector, settings.dateTimeSupport);
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

    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    @Getter
    final class Record {
        private final Runnable handler;
        private final long executionDateTime;
    }

    enum Type {
        EXPLICIT_DELAY,
        NO_DELAY
    }

    interface Selector {
        int next();

        int size();
    }

    @Builder
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    final class Settings {
        private static final Map<Type, EventLoopDefault.FactoryProxy> EVENT_LOOP_FACTORIES = createEventLoopFactories();
        private static final Map<Boolean, EventLoopDefault.EventRecordsFactory> EVENT_RECORDS_FACTORIES = createEventRecordsFactories();
        @Builder.Default
        private final DateTimeSupport dateTimeSupport = DateTimeSupport.createMilliseconds();
        @Builder.Default
        private final Type type = Type.EXPLICIT_DELAY;
        private final String name;
        @Builder.Default
        private final int count = 1;
        private final Selector selector;
        private final ExceptionLogger exceptionLogger;
        @Builder.Default
        private final boolean contended = true;
        private final ExecutorService executorService;

        private static Map<Type, EventLoopDefault.FactoryProxy> createEventLoopFactories() {
            Map<Type, EventLoopDefault.FactoryProxy> eventLoopFactories = new EnumMap<>(Type.class);

            eventLoopFactories.put(Type.EXPLICIT_DELAY, EventLoopExplicitDelay::new);
            eventLoopFactories.put(Type.NO_DELAY, EventLoopNoDelay::new);

            return eventLoopFactories;
        }

        private static Map<Boolean, EventLoopDefault.EventRecordsFactory> createEventRecordsFactories() {
            Map<Boolean, EventLoopDefault.EventRecordsFactory> eventLoopFactories = new HashMap<>();

            eventLoopFactories.put(true, q -> new ExclusiveQueueLocked<>(new ReentrantLock(), q));
            eventLoopFactories.put(false, ExclusiveQueueUnlocked::new);

            return eventLoopFactories;
        }
    }
}
