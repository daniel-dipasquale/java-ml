package com.dipasquale.ai.rl.neat.common.game2048;

import com.dipasquale.search.mcts.SearchNode;
import com.dipasquale.search.mcts.alphazero.AlphaZeroEdge;
import com.dipasquale.search.mcts.alphazero.AlphaZeroPolicyDistributor;
import com.dipasquale.simulation.game2048.GameAction;
import com.dipasquale.simulation.game2048.GameState;

final class GameAlphaZeroPolicyDistributor implements AlphaZeroPolicyDistributor<GameAction, GameState> {
    private static final float PROBABILITY_OF_SPAWNING_2 = 0.9f;
    private static final float[] EXPLORATION_PROBABILITIES = {PROBABILITY_OF_SPAWNING_2, 1f - PROBABILITY_OF_SPAWNING_2};

    @Override
    public float distribute(final SearchNode<GameAction, AlphaZeroEdge, GameState> node, final float explorationProbability) {
        int value = node.getAction().getValuedTileAdded().getValue();

        return EXPLORATION_PROBABILITIES[value - 1] * explorationProbability;
    }
}
