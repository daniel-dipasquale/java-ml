package com.dipasquale.io;

import java.io.IOException;
import java.io.Serial;

public final class IORuntimeException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -9167459362778579640L;

    public IORuntimeException(final String message, final IOException cause) {
        super(message, cause);
    }
}
