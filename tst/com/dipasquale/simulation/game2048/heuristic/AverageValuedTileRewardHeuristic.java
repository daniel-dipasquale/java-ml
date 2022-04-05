package com.dipasquale.simulation.game2048.heuristic;

import com.dipasquale.search.mcts.common.RewardHeuristic;
import com.dipasquale.simulation.game2048.Game;
import com.dipasquale.simulation.game2048.GameAction;
import com.dipasquale.simulation.game2048.GameState;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AverageValuedTileRewardHeuristic implements RewardHeuristic<GameAction, GameState> {
    private static final OptimumValuedTile OPTIMUM_VALUED_TILE = OptimumValuedTile.getInstance();
    private static final AverageValuedTileRewardHeuristic INSTANCE = new AverageValuedTileRewardHeuristic();

    public static AverageValuedTileRewardHeuristic getInstance() {
        return INSTANCE;
    }

    private static float calculateMaximumAverage(final int depth, final int valuedTileCount) {
        List<Integer> maximumValuedTiles = OPTIMUM_VALUED_TILE.getMaximumValuedTiles(depth, valuedTileCount);
        int maximum = 0;

        for (int maximumValuedTile : maximumValuedTiles) {
            maximum += Game.toDisplayValue(maximumValuedTile);
        }

        return (float) maximum / (float) valuedTileCount;
    }

    private static String createMessage(final GameState state, final float average, final float maximum) {
        return String.format("depth: %d, average: %f, maximum: %f, tile count (for average): %d", state.getDepth(), average, maximum, state.getValuedTileCount());
    }

    @Override
    public float estimate(final GameState state) {
        int sum = 0;

        for (int tileId = 0; tileId < Game.BOARD_SQUARE_LENGTH; tileId++) {
            int value = state.getValueInTile(tileId);

            if (value > 0) {
                sum += Game.toDisplayValue(value);
            }
        }

        int valuedTileCount = state.getValuedTileCount();
        float average = (float) sum / (float) valuedTileCount;
        float maximum = calculateMaximumAverage(state.getDepth(), valuedTileCount);

        assert Float.compare(average, maximum) <= 0 : createMessage(state, average, maximum);

        return RewardHeuristic.convertProbability(average / maximum);
    }
}
