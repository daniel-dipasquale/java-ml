package com.dipasquale.io.serialization.json;

import java.io.Serial;

public final class UnableToCloneJsonObjectException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -8744911448095524806L;
    private static final String MESSAGE = "unable to clone the JsonObject";

    UnableToCloneJsonObjectException(final Throwable cause) {
        super(MESSAGE, cause);
    }
}
