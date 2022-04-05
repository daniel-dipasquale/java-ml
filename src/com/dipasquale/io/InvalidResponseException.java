package com.dipasquale.io;

import java.io.IOException;
import java.io.Serial;

public final class InvalidResponseException extends IOException {
    @Serial
    private static final long serialVersionUID = 1866369916098919967L;

    InvalidResponseException(final String message) {
        super(message);
    }
}
