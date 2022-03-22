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

    public boolean isSuccessful() {
        return state.getStatusId() == GameState.PLAYER_PARTICIPANT_ID;
    }

    public int getValueInTile(final int tileId) {
        return state.getValueInTile(tileId);
    }

    public int getValuedTileCount() {
        return state.getValuedTileCount();
    }

    public int getScore() {
        return state.getScore();
    }

    public int getMoveCount() {
        return state.getDepth() / 2;
    }
}
