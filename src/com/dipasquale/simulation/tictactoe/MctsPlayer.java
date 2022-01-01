package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.classic.ClassicSearchEdge;
import com.dipasquale.search.mcts.core.MonteCarloTreeSearch;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class MctsPlayer implements Player {
    private final MonteCarloTreeSearch<GameState, ClassicSearchEdge> mcts;

    @Override
    public GameState createNextState(final GameEnvironment environment) {
        return mcts.findNextState(environment);
    }
}
