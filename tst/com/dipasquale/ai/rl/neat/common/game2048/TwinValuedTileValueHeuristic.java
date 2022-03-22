package com.dipasquale.ai.rl.neat.common.game2048;

import com.dipasquale.search.mcts.common.ValueHeuristic;
import com.dipasquale.simulation.game2048.GameAction;
import com.dipasquale.simulation.game2048.GameState;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class TwinValuedTileValueHeuristic implements ValueHeuristic<GameAction, GameState> {
    private static final int DIMENSION = 4;
    private static final int LENGTH = DIMENSION * DIMENSION;
    private static final float MAXIMUM_TWIN_TILE_COUNT = 48f;

    @Override
    public float estimate(final GameState state) {
        int twinTileCount = 0;

        for (int tileId = 0; tileId < LENGTH; tileId++) {
            int value = state.getValueInTile(tileId);

            if (value > 0) {
                int relativeTileId = tileId % DIMENSION;
                int firstInRowTileId = tileId - relativeTileId;
                int relativeTileIdLeft = relativeTileId - 1;

                if (relativeTileIdLeft >= 0 && state.getValueInTile(firstInRowTileId + relativeTileIdLeft) == value) {
                    twinTileCount++;
                }

                int relativeTileIdUp = relativeTileId - DIMENSION;
                int tileIdUp = firstInRowTileId + relativeTileIdUp;

                if (tileIdUp >= 0 && state.getValueInTile(tileIdUp) == value) {
                    twinTileCount++;
                }

                int relativeTileIdRight = relativeTileId + 1;

                if (relativeTileIdRight < DIMENSION && state.getValueInTile(firstInRowTileId + relativeTileIdRight) == value) {
                    twinTileCount++;
                }

                int relativeTileIdDown = relativeTileId + DIMENSION;
                int tileIdDown = firstInRowTileId + relativeTileIdDown;

                if (tileIdDown < LENGTH && state.getValueInTile(tileIdDown) == value) {
                    twinTileCount++;
                }
            }
        }

        float twinTileRate = (float) twinTileCount / MAXIMUM_TWIN_TILE_COUNT;

        return ValueHeuristic.convertProbability(twinTileRate);
    }
}
