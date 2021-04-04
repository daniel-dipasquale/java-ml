package com.dipasquale.common;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class MultiExceptionHandler<T> {
    private final List<T> items;
    private final ItemHandler<T> handler;

    public <E extends Exception> void invokeAllAndReportAsSuppressed(final ExceptionHandlerUtils.Factory<E> exceptionFactory)
            throws E {
        List<Throwable> suppressed = new ArrayList<>();

        for (T item : items) {
            try {
                handler.handle(item);
            } catch (Throwable e) {
                suppressed.add(e);
            }
        }

        ExceptionHandlerUtils.throwAsSuppressedIfAny(exceptionFactory, suppressed);
    }

    public void invokeAllAndReportAsSuppressed(final String message) {
        invokeAllAndReportAsSuppressed(() -> new RuntimeException(message));
    }

    @FunctionalInterface
    public interface ItemHandler<T> {
        void handle(T item) throws Exception;
    }
}
