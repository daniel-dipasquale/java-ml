package com.dipasquale.common;

import lombok.Getter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class ExceptionSupport {
    @Getter
    private static final ExceptionSupport instance = new ExceptionSupport();

    public <T extends Exception> void throwAsSuppressedIfAny(final Factory<T> exceptionFactory, final List<Throwable> suppressed)
            throws T {
        if (suppressed.size() == 0) {
            return;
        }

        T exception = exceptionFactory.create();

        suppressed.forEach(exception::addSuppressed);

        throw exception;
    }

    public void throwAsSuppressedIfAny(final String message, final List<Throwable> suppressed) {
        throwAsSuppressedIfAny(() -> new RuntimeException(message), suppressed);
    }

    public <TItem, TException extends Exception> void invokeAllAndThrowAsSuppressedIfAny(final List<TItem> items, final ItemHandler<TItem> itemHandler, final Factory<TException> exceptionFactory)
            throws TException {
        List<Throwable> suppressed = new ArrayList<>();

        for (TItem item : items) {
            try {
                itemHandler.handle(item);
            } catch (Throwable e) {
                suppressed.add(e);
            }
        }

        throwAsSuppressedIfAny(exceptionFactory, suppressed);
    }

    public <T> void invokeAllAndThrowAsSuppressedIfAny(final List<T> items, final ItemHandler<T> itemHandler, final String message) {
        invokeAllAndThrowAsSuppressedIfAny(items, itemHandler, () -> new RuntimeException(message));
    }

    public void print(final OutputStream outputStream, final Throwable exception, final boolean autoFlush, final Charset charset)
            throws IOException {
        try (PrintStream printStream = new PrintStream(outputStream, autoFlush, charset.toString())) {
            exception.printStackTrace(printStream);

            for (Throwable e = exception.getCause(); e != null; e = e.getCause()) {
                printStream.write("Caused by: ".getBytes(charset));
                e.printStackTrace(printStream);
            }
        }
    }

    @FunctionalInterface
    public interface Factory<T extends Exception> {
        T create();
    }

    @FunctionalInterface
    public interface ItemHandler<T> {
        void handle(T item);
    }
}
