package com.dipasquale.simulation.game2048.heuristic;

import com.dipasquale.search.mcts.common.ExplorationHeuristic;
import com.dipasquale.simulation.game2048.Game;
import com.dipasquale.simulation.game2048.GameAction;
import com.dipasquale.simulation.game2048.ValuedTile;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameExplorationHeuristic implements ExplorationHeuristic<GameAction> {
    private static final float[] EXPLORATION_PROBABILITIES = {Game.PROBABILITY_OF_SPAWNING_2, 1f - Game.PROBABILITY_OF_SPAWNING_2};
    private static final GameExplorationHeuristic INSTANCE = new GameExplorationHeuristic();

    public static GameExplorationHeuristic getInstance() {
        return INSTANCE;
    }

    @Override
    public float estimate(final GameAction action) {
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
