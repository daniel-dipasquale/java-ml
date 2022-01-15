package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.synchronization.wait.handle.InteractiveWaitHandle;
import com.dipasquale.synchronization.wait.handle.WaitHandle;

import java.util.Objects;

public interface EventLoop extends WaitHandle {
    String getName();

    int getConcurrencyLevel();

    void queue(EventLoopHandler handler, long delayTime, ErrorHandler errorHandler, InteractiveWaitHandle invokedWaitHandle);

    default void queue(final EventLoopHandler handler, final long delayTime, final InteractiveWaitHandle invokedWaitHandle) {
        queue(handler, delayTime, null, invokedWaitHandle);
    }

    default void queue(final EventLoopHandler handler, final long delayTime) {
        queue(handler, delayTime, null, null);
    }

    void queue(IntervalEventLoopHandler handler, long delayTime, ErrorHandler errorHandler, InteractiveWaitHandle invokedWaitHandle);

    default void queue(final IntervalEventLoopHandler handler, final long delayTime, final InteractiveWaitHandle invokedWaitHandle) {
        queue(handler, delayTime, null, invokedWaitHandle);
    }

    default void queue(final IntervalEventLoopHandler handler, final long delayTime) {
        queue(handler, delayTime, null, null);
    }

    boolean isEmpty();

    void clear();

    void shutdown();

    private static EventLoopFactory.Proxy createFactoryProxy(final EventLoopSettings settings, final EventLoopParams params) {
        int[] index = new int[]{0};

        return entryPoint -> settings.getFactory().create(String.format("%s-%d", settings.getName(), ++index[0]), params, entryPoint);
    }

    private static EventLoopSelector createSelector(final EventLoopSettings settings) {
        return Objects.requireNonNullElseGet(settings.getEventLoopSelector(), ThreadLocalRandomEventLoopSelector::getInstance);
    }

    static EventLoop create(final EventLoopSettings settings) {
        ArgumentValidatorSupport.ensureGreaterThanZero(settings.getConcurrencyLevel(), "settings.concurrencyLevel");

        EventLoopParams params = EventLoopParams.builder()
                .executorService(settings.getExecutorService())
                .dateTimeSupport(settings.getDateTimeSupport())
                .errorHandler(settings.getErrorHandler())
                .build();

        if (settings.getConcurrencyLevel() == 1) {
            return settings.getFactory().create(settings.getName(), params, null);
        }

        String name = String.format("%s-router", settings.getName());
        EventLoopFactory.Proxy eventLoopFactoryProxy = createFactoryProxy(settings, params);
        EventLoopSelector eventLoopSelector = createSelector(settings);

        return new RouterEventLoop(name, eventLoopFactoryProxy, settings.getConcurrencyLevel(), eventLoopSelector, settings.getDateTimeSupport());
    }
}
