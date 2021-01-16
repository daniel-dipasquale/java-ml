package com.dipasquale.common;

import lombok.Getter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.List;

public final class ExceptionHandlerSupport {
    @Getter
    private static final ExceptionHandlerSupport instance = new ExceptionHandlerSupport();

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
}
