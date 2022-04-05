package com.dipasquale.simulation.game2048;

import java.io.Serial;

public final class IllegalGameActionException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 8881230558290086874L;

    IllegalGameActionException(final String message) {
        super(message);
    }
}
