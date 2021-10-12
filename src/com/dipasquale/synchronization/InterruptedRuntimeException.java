package com.dipasquale.synchronization;

import java.io.Serial;

public final class InterruptedRuntimeException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -4606238049524525105L;

    public InterruptedRuntimeException(final String message, final InterruptedException cause) {
        super(message, cause);
    }
}
