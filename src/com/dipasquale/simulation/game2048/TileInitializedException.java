package com.dipasquale.simulation.game2048;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

public final class TileInitializedException extends RuntimeException implements Serializable {
    @Serial
    private static final long serialVersionUID = 443774894094327968L;
    @Getter
    private final int tileId;

    private static String createMessage(final int tileId) {
        return String.format("the tileId (%d) is already initialized", tileId);
    }

    public TileInitializedException(final int tileId) {
        super(createMessage(tileId));
        this.tileId = tileId;
    }
}
