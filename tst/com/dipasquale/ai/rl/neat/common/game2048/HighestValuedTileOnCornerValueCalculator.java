package com.dipasquale.ai.rl.neat.common.game2048;

import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.alphazero.AlphaZeroValueCalculator;
import com.dipasquale.simulation.game2048.GameAction;
import com.dipasquale.simulation.game2048.GameState;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class HighestValuedTileOnCornerValueCalculator implements AlphaZeroValueCalculator<GameAction, GameState> {
    private static final int DIMENSION = 4;
    private static final int LENGTH = DIMENSION * DIMENSION;

    private static boolean isSafeIfMovedLeft(final GameState state, final int value, final int relativeTileId, final int firstInRowTileId) {
        for (int i = relativeTileId - 1, currentValue = value; i >= 0; i--) {
            int adjacentValue = state.getValueFromTile(firstInRowTileId + i);

            if (adjacentValue == currentValue) {
                currentValue++;
            } else if (adjacentValue != 0) {
                return false;
            }
        }

        return true;
    }

    private static boolean isSafeIfMovedUp(final GameState state, final int tileId, final int value) {
        for (int i = tileId - DIMENSION, currentValue = value; i >= 0; i -= DIMENSION) {
            int adjacentValue = state.getValueFromTile(i);

            if (adjacentValue == currentValue) {
                currentValue++;
            } else if (adjacentValue != 0) {
                return false;
            }
        }

        return true;
    }

    private static boolean isSafeIfMovedRight(final GameState state, final int value, final int relativeTileId, final int firstInRowTileId) {
        for (int i = relativeTileId + 1, currentValue = value; i < DIMENSION; i++) {
            int adjacentValue = state.getValueFromTile(firstInRowTileId + i);

            if (adjacentValue == currentValue) {
                currentValue++;
            } else if (adjacentValue != 0) {
                return false;
            }
        }

        return true;
    }

    private static boolean isSafeIfMovedDown(final GameState state, final int tileId, final int value) {
        for (int i = tileId + DIMENSION, currentValue = value; i < LENGTH; i += DIMENSION) {
            int adjacentValue = state.getValueFromTile(i);

            if (adjacentValue == currentValue) {
                currentValue++;
            } else if (adjacentValue != 0) {
                return false;
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
    public float calculate(final SearchNode<GameAction, AlphaZeroEdge, GameState> node) {
        GameState state = node.getState();
        float totalScore = 0f;

        for (int tileId = 0; tileId < LENGTH; tileId++) {
            int value = state.getValueFromTile(tileId);

            if (value > 0) {
                int relativeTileId = tileId % DIMENSION;
                int firstInRowTileId = tileId - relativeTileId;
                float score = calculateScore(state, tileId, value, relativeTileId, firstInRowTileId);

                totalScore += score;
            }
        }

        if (Float.compare(totalScore, 0f) == 0) {
            return 0f;
        }

        return (Math.abs(totalScore) - 2f) / totalScore;
    }
}
