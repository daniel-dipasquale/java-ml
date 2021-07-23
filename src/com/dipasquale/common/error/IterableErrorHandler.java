package com.dipasquale.common.error;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class IterableErrorHandler<T> {
    private final Iterable<T> items;
    private final ItemHandler<T> handler;

    public <E extends Throwable> void invokeAllAndReportAsSuppressed(final ErrorHandlerSupport.Factory<E> exceptionFactory)
            throws E {
        List<Throwable> suppressed = new ArrayList<>();

        for (T item : items) {
            try {
                handler.handle(item);
            } catch (Throwable e) {
                suppressed.add(e);
            }
        }

        ErrorHandlerSupport.throwAsSuppressedIfAny(exceptionFactory, suppressed);
    }

    public void invokeAllAndReportAsSuppressed(final String message) {
        invokeAllAndReportAsSuppressed(() -> new RuntimeException(message));
    }

    @FunctionalInterface
    public interface ItemHandler<T> {
        void handle(T item) throws Exception;
    }
}
