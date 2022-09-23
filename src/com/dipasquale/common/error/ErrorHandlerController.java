package com.dipasquale.common.error;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class ErrorHandlerController<T> {
    private final Iterable<T> elements;
    private final ElementHandler<T> handler;

    public <TError extends Throwable> void handleAll(final ErrorHandlerSupport.Factory<TError> errorFactory)
            throws TError {
        List<Throwable> suppressed = new ArrayList<>();

        for (T element : elements) {
            try {
                handler.handle(element);
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
    public interface ElementHandler<T> {
        void handle(T element) throws Exception;
    }
}
