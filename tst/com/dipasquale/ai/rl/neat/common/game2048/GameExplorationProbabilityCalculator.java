package com.dipasquale.ai.rl.neat.common.game2048;

import com.dipasquale.search.mcts.common.ExplorationProbabilityCalculator;
import com.dipasquale.simulation.game2048.GameAction;
import com.dipasquale.simulation.game2048.GameState;
import com.dipasquale.simulation.game2048.ValuedTile;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class GameExplorationProbabilityCalculator implements ExplorationProbabilityCalculator<GameAction> {
    private static final float[] EXPLORATION_PROBABILITIES = {GameState.PROBABILITY_OF_SPAWNING_2, 1f - GameState.PROBABILITY_OF_SPAWNING_2};
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
