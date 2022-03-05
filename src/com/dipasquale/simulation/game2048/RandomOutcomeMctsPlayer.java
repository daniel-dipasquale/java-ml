package com.dipasquale.simulation.game2048;

import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public final class RandomOutcomeMctsPlayer implements Player {
    private final MonteCarloTreeSearch<GameAction, ?, GameState> mcts;

    @Override
    public void initializeState(final GameState state) {
        state.initializeRandomTiles();
    }

    @Override
    public GameAction createNextAction(final GameState state) {
        return mcts.proposeNextAction(state);
    }
}
