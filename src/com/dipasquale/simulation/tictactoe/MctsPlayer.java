package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public final class MctsPlayer implements Player {
    private final MonteCarloTreeSearch<GameAction, GameState> mcts;

    @Override
    public GameAction createNextAction(final GameState state) {
        return mcts.proposeNextAction(state);
    }

    @Override
    public void accept(final GameResult result) {
        mcts.reset();
    }
}
