package com.dipasquale.common;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MultiExceptionHandler {
    private final List<?> items;
    private final ItemHandler<?> itemHandler;

    public static <T> MultiExceptionHandler create(final List<T> items, final ItemHandler<T> itemHandler) {
        return new MultiExceptionHandler(items, itemHandler);
    }

    private static <T> T ensureType(final Object object) {
        return (T) object;
    }

    public <T extends Exception> void invokeAllAndThrowAsSuppressedIfAny(final ExceptionHandlerUtils.Factory<T> exceptionFactory)
            throws T {
        List<Throwable> suppressed = new ArrayList<>();

        for (Object item : items) {
            try {
                itemHandler.handle(ensureType(item));
            } catch (Throwable e) {
                suppressed.add(e);
            }
        }

        ExceptionHandlerUtils.throwAsSuppressedIfAny(exceptionFactory, suppressed);
    }

    public void invokeAllAndThrowAsSuppressedIfAny(final String message) {
        invokeAllAndThrowAsSuppressedIfAny(() -> new RuntimeException(message));
    }

    @FunctionalInterface
    public interface ItemHandler<T> {
        void handle(T item) throws Exception;
    }
}
