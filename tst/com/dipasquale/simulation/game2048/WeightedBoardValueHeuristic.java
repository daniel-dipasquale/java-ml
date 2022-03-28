package com.dipasquale.simulation.game2048;

import com.dipasquale.search.mcts.common.ValueHeuristic;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class WeightedBoardValueHeuristic implements ValueHeuristic<GameAction, GameState> {
    private static final OptimumValuedTile OPTIMUM_VALUED_TILE = OptimumValuedTile.getInstance();
    private final WeightedBoardType type;

    @Override
    public float estimate(final GameState state) {
        double total = 0D;
        double[] weights = type.getWeights();

        for (int tileId = 0; tileId < weights.length; tileId++) {
            int value = state.getValueInTile(tileId);

            if (value > 0) {
                double weightedValue = (double) Game.getDisplayValue(value) * weights[tileId];

                total += weightedValue;
            }
        }

        List<Integer> maximumValuedTiles = OPTIMUM_VALUED_TILE.getMaximumValuedTiles(state.getDepth());
        double maximum = 0D;
        double[] sortedWeights = type.getSortedWeights();

        for (int i = 0, c = maximumValuedTiles.size(); i < c; i++) {
            double weightedValue = (double) Game.getDisplayValue(maximumValuedTiles.get(i)) * sortedWeights[i];

            maximum += weightedValue;
        }

        assert Double.compare(total, maximum) <= 0;

        return ValueHeuristic.convertProbability((float) total / (float) maximum);
    }
}
