package com.dipasquale.simulation.game2048;

import com.dipasquale.search.mcts.common.ValueHeuristic;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AverageValuedTileValueHeuristic implements ValueHeuristic<GameAction, GameState> {
    private static final OptimumValuedTile OPTIMUM_VALUED_TILE = OptimumValuedTile.getInstance();
    private static final AverageValuedTileValueHeuristic INSTANCE = new AverageValuedTileValueHeuristic();

    public static AverageValuedTileValueHeuristic getInstance() {
        return INSTANCE;
    }

    private static float calculateMaximumAverage(final GameState state) {
        int size = state.getValuedTileCount();
        List<Integer> maximumValuedTiles = OPTIMUM_VALUED_TILE.getMaximumValuedTiles(state.getDepth(), size);
        int maximum = 0;

        for (int maximumValuedTile : maximumValuedTiles) {
            maximum += Game.getDisplayValue(maximumValuedTile);
        }

        return (float) maximum / (float) size;
    }

    private static String createMessage(final GameState state, final float average, final float maximum) {
        return String.format("depth: %d, average: %f, maximum: %f, tile count (for average): %d", state.getDepth(), average, maximum, state.getValuedTileCount());
    }

    @Override
    public float estimate(final GameState state) {
        double sum = 0D;

        for (int tileId = 0; tileId < Game.BOARD_SQUARE_LENGTH; tileId++) {
            int value = state.getValueInTile(tileId);

            if (value > 0) {
                sum += Game.getDisplayValue(value);
            }
        }

        double average = sum / (double) state.getValuedTileCount();
        float fixedAverage = (float) average;
        float maximum = calculateMaximumAverage(state);

        assert Float.compare(fixedAverage, maximum) <= 0 : createMessage(state, fixedAverage, maximum);

        return ValueHeuristic.convertProbability(fixedAverage / maximum);
    }
}
