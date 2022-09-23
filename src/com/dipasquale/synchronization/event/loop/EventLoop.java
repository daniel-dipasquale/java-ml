package com.dipasquale.synchronization.event.loop;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.synchronization.wait.handle.InteractiveWaitHandle;
import com.dipasquale.synchronization.wait.handle.WaitHandle;

import java.util.Objects;
import java.util.Set;

public interface EventLoop extends WaitHandle {
    Set<Long> getThreadIds();

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
        return entryPoint -> settings.getFactory().create(params, entryPoint);
    }

    private static EventLoopSelector createSelector(final EventLoopSettings settings) {
        return Objects.requireNonNullElseGet(settings.getSelector(), RandomEventLoopSelector::getInstance);
    }

    static EventLoop create(final EventLoopSettings settings) {
        ArgumentValidatorSupport.ensureGreaterThanZero(settings.getConcurrencyLevel(), "settings.concurrencyLevel");

        EventLoopParams params = EventLoopParams.builder()
                .dateTimeSupport(settings.getDateTimeSupport())
                .errorHandler(settings.getErrorHandler())
                .build();

        if (settings.getConcurrencyLevel() == 1) {
            return settings.getFactory().create(params, null);
        }

        EventLoopFactory.Proxy factoryProxy = createFactoryProxy(settings, params);
        EventLoopSelector selector = createSelector(settings);

        return new RouterEventLoop(factoryProxy, settings.getConcurrencyLevel(), selector, settings.getDateTimeSupport());
    }
}
