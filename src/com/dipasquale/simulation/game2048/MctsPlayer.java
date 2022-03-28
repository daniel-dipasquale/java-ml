package com.dipasquale.simulation.game2048;

import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public final class MctsPlayer implements Player {
    private final MonteCarloTreeSearch<GameAction, GameState> mcts;
    private final boolean debug;

    @Override
    public GameAction createNextAction(final GameState state) {
        return mcts.proposeNextAction(state);
    }

    @Override
    public void accept(final GameState state) {
        mcts.reset();

        if (debug) {
            state.print(System.out);
        }
    }
}
