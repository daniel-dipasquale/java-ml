package com.dipasquale.common.error;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class IterableErrorHandler<T> {
    private final Iterable<T> items;
    private final ItemHandler<T> handler;

    public <TError extends Throwable> void handleAll(final ErrorHandlerSupport.Factory<TError> errorFactory)
            throws TError {
        List<Throwable> suppressed = new ArrayList<>();

        for (T item : items) {
            try {
                handler.handle(item);
            } catch (Throwable e) {
                suppressed.add(e);
            }
        }

        ErrorHandlerSupport.failAsSuppressedIfAny(errorFactory, suppressed);
    }

    public void handleAll(final String message) {
        handleAll(() -> new RuntimeException(message));
    }

    @FunctionalInterface
    public interface ItemHandler<T> {
        void handle(T item) throws Exception;
    }
}
