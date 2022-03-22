package com.dipasquale.ai.rl.neat.common.game2048;

import com.dipasquale.search.mcts.common.ValueHeuristic;
import com.dipasquale.simulation.game2048.GameAction;
import com.dipasquale.simulation.game2048.GameState;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class HighestValuedTileOnCornerValueHeuristic implements ValueHeuristic<GameAction, GameState> {
    private static final int DIMENSION = 4;
    private static final int LENGTH = DIMENSION * DIMENSION;

    private static boolean isSafeIfMovedLeft(final GameState state, final int value, final int relativeTileId, final int firstInRowTileId) {
        for (int i = relativeTileId - 1, currentValue = value; i >= 0; i--) {
            int adjacentValue = state.getValueInTile(firstInRowTileId + i);

            if (adjacentValue != 0 && adjacentValue != currentValue) {
                return false;
            }

            if (adjacentValue == currentValue) {
                currentValue++;
            }
        }

        return true;
    }

    private static boolean isSafeIfMovedUp(final GameState state, final int tileId, final int value) {
        for (int i = tileId - DIMENSION, currentValue = value; i >= 0; i -= DIMENSION) {
            int adjacentValue = state.getValueInTile(i);

            if (adjacentValue != 0 && adjacentValue != currentValue) {
                return false;
            }

            if (adjacentValue == currentValue) {
                currentValue++;
            }
        }

        return true;
    }

    private static boolean isSafeIfMovedRight(final GameState state, final int value, final int relativeTileId, final int firstInRowTileId) {
        for (int i = relativeTileId + 1, currentValue = value; i < DIMENSION; i++) {
            int adjacentValue = state.getValueInTile(firstInRowTileId + i);

            if (adjacentValue != 0 && adjacentValue != currentValue) {
                return false;
            }

            if (adjacentValue == currentValue) {
                currentValue++;
            }
        }

        return true;
    }

    private static boolean isSafeIfMovedDown(final GameState state, final int tileId, final int value) {
        for (int i = tileId + DIMENSION, currentValue = value; i < LENGTH; i += DIMENSION) {
            int adjacentValue = state.getValueInTile(i);

            if (adjacentValue != 0 && adjacentValue != currentValue) {
                return false;
            }

            if (adjacentValue == currentValue) {
                currentValue++;
            }
        }

        return true;
    }

    private static float calculateScore(final GameState state, final int tileId, final int value, final int relativeTileId, final int firstInRowTileId) {
        int safeCount = 0;

        if (isSafeIfMovedLeft(state, value, relativeTileId, firstInRowTileId)) {
            safeCount++;
        }

        if (isSafeIfMovedUp(state, tileId, value)) {
            safeCount++;
        }

        if (isSafeIfMovedRight(state, value, relativeTileId, firstInRowTileId)) {
            safeCount++;
        }

        if (isSafeIfMovedDown(state, tileId, value)) {
            safeCount++;
        }

        double rate = safeCount - 2;

        return (float) (Math.pow(2D, value) * rate);
    }

    @Override
    public float estimate(final GameState state) {
        float score = 0f;

        for (int tileId = 0; tileId < LENGTH; tileId++) {
            int value = state.getValueInTile(tileId);

            if (value > 0) {
                int relativeTileId = tileId % DIMENSION;
                int firstInRowTileId = tileId - relativeTileId;

                score += calculateScore(state, tileId, value, relativeTileId, firstInRowTileId);
            }
        }

        return ValueHeuristic.calculateUnbounded(score);
    }
}
