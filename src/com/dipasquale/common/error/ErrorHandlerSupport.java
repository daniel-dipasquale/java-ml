package com.dipasquale.common.error;

import com.dipasquale.common.factory.ObjectFactory;
import lombok.Generated;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.List;

public final class ErrorHandlerSupport {
    @Generated
    private ErrorHandlerSupport() {
    }

    public static <T extends Throwable> void failAsSuppressedIfAny(final Factory<T> errorFactory, final List<Throwable> suppressed)
            throws T {
        if (suppressed.isEmpty()) {
            return;
        }

        T exception = errorFactory.create();

        suppressed.forEach(exception::addSuppressed);

        throw exception;
    }

    public static void failAsSuppressedIfAny(final String message, final List<Throwable> suppressed) {
        failAsSuppressedIfAny(() -> new RuntimeException(message), suppressed);
    }

    public static void print(final OutputStream outputStream, final Throwable error, final boolean autoFlush, final Charset charset)
            throws IOException {
        try (PrintStream printStream = new PrintStream(outputStream, autoFlush, charset.toString())) {
            error.printStackTrace(printStream);

            for (Throwable e = error.getCause(); e != null; e = e.getCause()) {
                printStream.write("Caused by: ".getBytes(charset));
                e.printStackTrace(printStream);
            }
        }
    }

    @FunctionalInterface
    public interface Factory<T extends Throwable> extends ObjectFactory<T> {
    }
}
