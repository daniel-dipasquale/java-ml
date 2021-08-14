package com.dipasquale.threading.event.loop;

import com.dipasquale.common.ArgumentValidatorSupport;
import com.dipasquale.common.error.ErrorHandler;
import com.dipasquale.threading.wait.handle.InteractiveWaitHandle;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface EventLoop {
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

    void awaitUntilEmpty() throws InterruptedException;

    boolean awaitUntilEmpty(long timeout, TimeUnit unit) throws InterruptedException;

    void shutdown();

    static EventLoop create(final EventLoopSettings settings) {
        ArgumentValidatorSupport.ensureGreaterThanZero(settings.getConcurrencyLevel(), "settings.concurrencyLevel");

        EventLoopParams params = EventLoopParams.builder()
                .eventRecordQueueFactory(ReentrantExclusiveQueue::new)
                .executorService(settings.getExecutorService())
                .dateTimeSupport(settings.getDateTimeSupport())
                .errorHandler(settings.getErrorHandler())
                .build();

        if (settings.getConcurrencyLevel() == 1) {
            return settings.getFactory().create(settings.getName(), params, null);
        }

        String name = String.format("%s-router", settings.getName());
        int[] index = new int[1];
        EventLoopFactory.Proxy eventLoopFactoryProxy = nep -> settings.getFactory().create(String.format("%s-%d", settings.getName(), ++index[0]), params, nep);

        EventLoopSelector eventLoopSelector = Optional.ofNullable(settings.getEventLoopSelector())
                .orElseGet(() -> new ThreadLocalRandomEventLoopSelector(settings.getConcurrencyLevel()));

        return new RouterEventLoop(name, eventLoopFactoryProxy, eventLoopSelector, settings.getDateTimeSupport());
    }
}
