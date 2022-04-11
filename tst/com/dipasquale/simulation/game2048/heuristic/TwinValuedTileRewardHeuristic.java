package com.dipasquale.simulation.game2048.heuristic;

import com.dipasquale.search.mcts.common.RewardHeuristic;
import com.dipasquale.simulation.game2048.Game;
import com.dipasquale.simulation.game2048.GameAction;
import com.dipasquale.simulation.game2048.GameState;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class TwinValuedTileRewardHeuristic implements RewardHeuristic<GameAction, GameState> {
    private static final int[] POSSIBLE_TWIN_COUNT = {
            2, 3, 3, 2,
            3, 4, 4, 3,
            3, 4, 4, 3,
            2, 3, 3, 2
    };

    private static final OptimumValuedTile OPTIMUM_VALUED_TILE = OptimumValuedTile.getInstance();
    private static final TwinValuedTileRewardHeuristic INSTANCE = new TwinValuedTileRewardHeuristic();

    public static TwinValuedTileRewardHeuristic getInstance() {
        return INSTANCE;
    }

    private static boolean isLeftTileTwin(final GameState state, final int value, final int relativeTileId, final int firstInRowTileId) {
        for (int offsetTileId = relativeTileId - 1; offsetTileId >= 0; offsetTileId--) {
            int twinTileId = firstInRowTileId + offsetTileId;
            int twinValue = state.getValueInTile(twinTileId);

            if (twinValue != 0) {
                return twinValue == value;
            }
        }

        return false;
    }

    private static boolean isUpTileTwin(final GameState state, final int value, final int tileId) {
        for (int twinTileId = tileId - Game.BOARD_ONE_DIMENSION_LENGTH; twinTileId >= 0; twinTileId -= Game.BOARD_ONE_DIMENSION_LENGTH) {
            int twinValue = state.getValueInTile(twinTileId);

            if (twinValue != 0) {
                return twinValue == value;
            }
        }

        return false;
    }

    private static boolean isRightTileTwin(final GameState state, final int value, final int relativeTileId, final int firstInRowTileId) {
        for (int offsetTileId = relativeTileId + 1; offsetTileId < Game.BOARD_ONE_DIMENSION_LENGTH; offsetTileId++) {
            int twinTileId = firstInRowTileId + offsetTileId;
            int twinValue = state.getValueInTile(twinTileId);

            if (twinValue != 0) {
                return twinValue == value;
            }
        }

        return false;
    }

    private static boolean isDownTileTwin(final GameState state, final int value, final int tileId) {
        for (int twinTileId = tileId + Game.BOARD_ONE_DIMENSION_LENGTH; twinTileId < Game.BOARD_SQUARE_LENGTH; twinTileId += Game.BOARD_ONE_DIMENSION_LENGTH) {
            int twinValue = state.getValueInTile(twinTileId);

            if (twinValue != 0) {
                return twinValue == value;
            }
        }

        return false;
    }

    @Override
    public float estimate(final GameState state) {
        int maximumValue = OPTIMUM_VALUED_TILE.getMaximumValuedTiles(state.getDepth()).get(0);
        int[] twinTileCounters = new int[maximumValue];
        int[] maximumTwinTileCounters = new int[maximumValue];

        for (int tileId = 0; tileId < Game.BOARD_SQUARE_LENGTH; tileId++) {
            int value = state.getValueInTile(tileId);

            if (value > 0) {
                int relativeTileId = tileId % Game.BOARD_ONE_DIMENSION_LENGTH;
                int firstInRowTileId = tileId - relativeTileId;
                int valueIndex = value - 1;
                int twinTileCounter = twinTileCounters[valueIndex];

                if (isLeftTileTwin(state, value, relativeTileId, firstInRowTileId)) {
                    twinTileCounters[valueIndex]++;
                }

                if (isUpTileTwin(state, value, tileId)) {
                    twinTileCounters[valueIndex]++;
                }

                if (isRightTileTwin(state, value, relativeTileId, firstInRowTileId)) {
                    twinTileCounters[valueIndex]++;
                }

                if (isDownTileTwin(state, value, tileId)) {
                    twinTileCounters[valueIndex]++;
                }

                if (twinTileCounter == twinTileCounters[valueIndex]) {
                    twinTileCounters[valueIndex] -= POSSIBLE_TWIN_COUNT[tileId];
                }

                maximumTwinTileCounters[valueIndex] += POSSIBLE_TWIN_COUNT[tileId];
            }
        }

        int score = 0;
        int maximumScore = 0;

        for (int i = maximumValue - 1; i >= 0; i--) {
            int translatedValue = Game.toDisplayValue(i + 1);

            score += twinTileCounters[i] * translatedValue;
            maximumScore += maximumTwinTileCounters[i] * translatedValue;
        }

        float twinTileRate = (float) (score + maximumScore) / (float) (maximumScore * 2);

        return RewardHeuristic.convertProbability(twinTileRate);
    }
}
