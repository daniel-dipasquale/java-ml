package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public final class MctsPlayer implements Player {
    private final MonteCarloTreeSearch<GameAction, ?, GameState> mcts;

    @Override
    public GameAction createNextState(final GameState environment) {
        return mcts.proposeNextState(environment);
    }
}
