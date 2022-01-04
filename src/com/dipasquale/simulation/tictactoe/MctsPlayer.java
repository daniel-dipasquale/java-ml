package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public final class MctsPlayer implements Player {
    private final MonteCarloTreeSearch<GameState, ?, GameEnvironment> mcts;

    @Override
    public GameState createNextState(final GameEnvironment environment) {
        return mcts.findNextState(environment);
    }
}
