package com.dipasquale.io.serialization.json.parser;

import java.io.Serial;

public final class UnableToParseJsonException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 4220897852805152707L;

    UnableToParseJsonException(final String message) {
        super(message);
    }
}
