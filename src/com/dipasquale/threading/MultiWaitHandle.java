package com.dipasquale.threading;

import com.dipasquale.common.DateTimeSupport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MultiWaitHandle {
    private final DateTimeSupport dateTimeSupport;
    private final HandlerInvocationPredicate handlerInvocationPredicate;
    private final List<?> waitHandles;
    private final IndefiniteHandler<?> indefinite;
    private final TimedHandler<?> timedHandler;

    public static <T> MultiWaitHandle create(final DateTimeSupport dateTimeSupport, final HandlerInvocationPredicate handlerInvocationPredicate, final List<T> waitHandles, final IndefiniteHandler<T> indefinite, final TimedHandler<T> timedHandler) {
        return new MultiWaitHandle(dateTimeSupport, handlerInvocationPredicate, waitHandles, indefinite, timedHandler);
    }

    public static <T> MultiWaitHandle createSinglePass(final DateTimeSupport dateTimeSupport, final List<T> waitHandles, final IndefiniteHandler<T> indefinite, final TimedHandler<T> timedHandler) {
        HandlerInvocationPredicate handlerInvocationPredicate = a -> a == 1;

        return create(dateTimeSupport, handlerInvocationPredicate, waitHandles, indefinite, timedHandler);
    }

    private static <T> T ensureType(final Object object) {
        return (T) object;
    }

    public void await()
            throws InterruptedException {
        for (int attempt = 0; handlerInvocationPredicate.shouldAwait(++attempt); ) {
            for (Object waitHandle : waitHandles) {
                indefinite.await(ensureType(waitHandle));
            }
        }
    }

    public boolean await(final long timeout, final TimeUnit unit)
            throws InterruptedException {
        boolean acquired = true;
        long offsetDateTime = dateTimeSupport.now();
        long timeoutRemaining = (long) DateTimeSupport.getUnit(unit).getConverterTo(dateTimeSupport.unit()).convert((double) timeout); // TODO: consider caching converters in a thread safe manner

        for (int attempt = 0; acquired && timeoutRemaining > 0L && handlerInvocationPredicate.shouldAwait(++attempt); ) {
            for (int i = 0, c = waitHandles.size(); acquired && timeoutRemaining > 0L; i = (i + 1) % c) {
                acquired = timedHandler.await(ensureType(waitHandles.get(i)), timeoutRemaining, dateTimeSupport.timeUnit());

                long currentDateTime = dateTimeSupport.now();

                timeoutRemaining -= currentDateTime - offsetDateTime;
                offsetDateTime = currentDateTime;
            }
        }

        return acquired;
    }

    @FunctionalInterface
    public interface HandlerInvocationPredicate {
        boolean shouldAwait(int attempt);
    }

    @FunctionalInterface
    public interface IndefiniteHandler<T> {
        void await(T waitHandle) throws InterruptedException;
    }

    @FunctionalInterface
    public interface TimedHandler<T> {
        boolean await(T waitHandle, long timeout, TimeUnit unit) throws InterruptedException;
    }
}
