package com.dipasquale.simulation.game2048;

import java.io.Serial;

public final class UnableToCreateGameActionException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 3371000001063362428L;

    UnableToCreateGameActionException(final String message) {
        super(message);
    }
}
