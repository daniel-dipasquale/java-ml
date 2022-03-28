package com.dipasquale.simulation.game2048;

import com.dipasquale.search.mcts.common.ValueHeuristic;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UniformityValueHeuristic implements ValueHeuristic<GameAction, GameState> {
    private static final float MAXIMUM_SCORE = (float) Math.pow(Game.BOARD_SQUARE_LENGTH, 3D);
    private static final OptimumValuedTile OPTIMUM_VALUED_TILE = OptimumValuedTile.getInstance();
    private static final UniformityValueHeuristic INSTANCE = new UniformityValueHeuristic();

    public static UniformityValueHeuristic getInstance() {
        return INSTANCE;
    }

    @Override
    public float estimate(final GameState state) {
        int maximumValue = OPTIMUM_VALUED_TILE.getMaximumValuedTiles(state.getDepth()).get(0);
        int[] valuedTileCounters = new int[maximumValue];

        for (int tileId = 0; tileId < Game.BOARD_SQUARE_LENGTH; tileId++) {
            int value = state.getValueInTile(tileId);

            if (value > 0) {
                int valueIndex = value - 1;

                valuedTileCounters[valueIndex]++;
            }
        }

        double score = 0D;

        for (int i = 0; i < maximumValue; i++) {
            score += Math.pow(valuedTileCounters[i], 3D);
        }

        float rate = (float) score / MAXIMUM_SCORE;

        return ValueHeuristic.convertProbability(rate);
    }
}
