package com.dipasquale.io;

import java.io.IOException;
import java.io.Serial;

public final class FailedToConnectException extends IOException {
    @Serial
    private static final long serialVersionUID = 6076021951033198557L;

    FailedToConnectException(final String message, final IOException cause) {
        super(message, cause);
    }
}
