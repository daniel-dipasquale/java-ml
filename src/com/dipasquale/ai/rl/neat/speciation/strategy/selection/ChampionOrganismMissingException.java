package com.dipasquale.ai.rl.neat.speciation.strategy.selection;

import java.io.Serial;

public final class ChampionOrganismMissingException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 3910227358859562088L;

    public ChampionOrganismMissingException(final String message) {
        super(message);
    }

    public ChampionOrganismMissingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
