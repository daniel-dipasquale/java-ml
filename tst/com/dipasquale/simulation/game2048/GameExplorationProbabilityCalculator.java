package com.dipasquale.simulation.game2048;

import com.dipasquale.search.mcts.common.ExplorationProbabilityCalculator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameExplorationProbabilityCalculator implements ExplorationProbabilityCalculator<GameAction> {
    private static final float[] EXPLORATION_PROBABILITIES = {Game.PROBABILITY_OF_SPAWNING_2, 1f - Game.PROBABILITY_OF_SPAWNING_2};
    private static final GameExplorationProbabilityCalculator INSTANCE = new GameExplorationProbabilityCalculator();

    public static GameExplorationProbabilityCalculator getInstance() {
        return INSTANCE;
    }

    @Override
    public float calculate(final GameAction action) {
        List<ValuedTile> valuedTiles = action.getValuedTilesAdded();
        int size = valuedTiles.size();
        int value = valuedTiles.get(0).getValue();
        float policy = EXPLORATION_PROBABILITIES[value - 1];

        if (size > 1) {
            for (int i = 1; i < size; i++) {
                value = valuedTiles.get(i).getValue();
                policy *= EXPLORATION_PROBABILITIES[value - 1];
            }
        }

        return policy;
    }
}
