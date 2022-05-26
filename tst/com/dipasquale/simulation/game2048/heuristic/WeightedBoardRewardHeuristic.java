package com.dipasquale.simulation.game2048.heuristic;

import com.dipasquale.search.mcts.heuristic.selection.RewardHeuristic;
import com.dipasquale.simulation.game2048.Game;
import com.dipasquale.simulation.game2048.GameAction;
import com.dipasquale.simulation.game2048.GameState;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class WeightedBoardRewardHeuristic implements RewardHeuristic<GameAction, GameState> {
    private static final OptimumValuedTile OPTIMUM_VALUED_TILE = OptimumValuedTile.getInstance();
    private final WeightedBoardType type;

    @Override
    public float estimate(final GameState state) {
        double sum = 0D;
        double[] weights = type.getWeights();

        for (int tileId = 0; tileId < weights.length; tileId++) {
            int value = state.getValueInTile(tileId);

            if (value > 0) {
                double weightedValue = (double) Game.toDisplayValue(value) * weights[tileId];

                sum += weightedValue;
            }
        }

        List<Integer> maximumValuedTiles = OPTIMUM_VALUED_TILE.getMaximumValuedTiles(state.getDepth());
        double maximum = 0D;
        double[] sortedWeights = type.getSortedWeights();

        for (int i = 0, c = maximumValuedTiles.size(); i < c; i++) {
            double weightedValue = (double) Game.toDisplayValue(maximumValuedTiles.get(i)) * sortedWeights[i];

            maximum += weightedValue;
        }

        assert Double.compare(sum, maximum) <= 0;

        return RewardHeuristic.convertProbability((float) sum / (float) maximum);
    }
}
