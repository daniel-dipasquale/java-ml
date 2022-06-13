package com.dipasquale.simulation.game2048;

import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.SearchNodeResult;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public final class MctsPlayer implements Player {
    private final MonteCarloTreeSearch<GameAction, GameState> mcts;
    private final boolean debug;

    @Override
    public SearchNodeResult<GameAction, GameState> produceNext(final SearchNodeResult<GameAction, GameState> searchNodeResult) {
        return mcts.proposeNext(searchNodeResult);
    }

    @Override
    public void accept(final SearchNodeResult<GameAction, GameState> searchNodeResult) {
        mcts.reset();

        if (debug) {
            searchNodeResult.getState().print(System.out);
        }
    }
}
