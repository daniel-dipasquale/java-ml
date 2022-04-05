package com.dipasquale.io;

import java.io.IOException;
import java.io.Serial;

public final class ConnectionClosedException extends IOException {
    @Serial
    private static final long serialVersionUID = 9068957149802633882L;

    ConnectionClosedException(final String message) {
        super(message);
    }
}
