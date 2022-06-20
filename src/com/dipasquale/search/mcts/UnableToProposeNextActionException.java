package com.dipasquale.search.mcts;

import java.io.Serial;

public final class UnableToProposeNextActionException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -8266074636413331087L;
    private static final String MESSAGE = "unable to suggest which action to take next";

    UnableToProposeNextActionException() {
        super(MESSAGE);
    }
}
