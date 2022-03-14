package com.dipasquale.simulation.game2048;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@ToString
public final class GameResult {
    private final GameState state;

    public boolean isSuccess() {
        return state.getStatusId() == GameState.PARTICIPANT_ID;
    }

    public int getValueFromTile(final int tileId) {
        return state.getValueFromTile(tileId);
    }

    public int getValuedTileCount() {
        return state.getValuedTileCount();
    }

    public int getScore() {
        return state.getScore();
    }

    public int getMoveCount() {
        return state.getDepth();
    }
}
