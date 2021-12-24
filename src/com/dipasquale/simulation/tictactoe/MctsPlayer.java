package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class MctsPlayer implements Player {
    private final MonteCarloTreeSearch<GameState> mcts;

    @Override
    public GameState createNextState(final GameEnvironment environment) {
        return mcts.findNextState(environment);
    }
}
