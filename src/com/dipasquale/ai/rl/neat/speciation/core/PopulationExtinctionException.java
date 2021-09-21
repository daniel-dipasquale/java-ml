package com.dipasquale.ai.rl.neat.speciation.core;

import java.io.Serial;

public final class PopulationExtinctionException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 44568232965014397L;

    public PopulationExtinctionException(final String message) {
        super(message);
    }

    public PopulationExtinctionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
