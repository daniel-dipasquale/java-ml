package com.dipasquale.io;

import java.io.IOException;
import java.io.Serial;

public final class ReachedEndOfReaderException extends IOException {
    @Serial
    private static final long serialVersionUID = 7194079544989992518L;
    private static final String MESSAGE = "reached end of reader";

    ReachedEndOfReaderException() {
        super(MESSAGE);
    }
}
