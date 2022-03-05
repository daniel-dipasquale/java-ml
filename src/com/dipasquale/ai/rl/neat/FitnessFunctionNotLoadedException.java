package com.dipasquale.ai.rl.neat;

import java.io.Serial;

public final class FitnessFunctionNotLoadedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 3450761250037905611L;

    public FitnessFunctionNotLoadedException(final String message) {
        super(message);
    }

    public FitnessFunctionNotLoadedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
