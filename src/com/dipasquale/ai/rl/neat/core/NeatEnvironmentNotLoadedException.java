package com.dipasquale.ai.rl.neat.core;

import java.io.Serial;

public final class NeatEnvironmentNotLoadedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 3450761250037905611L;
    private static final String MESSAGE = "unable to load the neat environment (aka: fitness function)";

    public NeatEnvironmentNotLoadedException() {
        super(MESSAGE);
    }

    public NeatEnvironmentNotLoadedException(final Throwable cause) {
        super(cause);
    }
}
