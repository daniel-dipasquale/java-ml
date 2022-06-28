package com.dipasquale.simulation.game2048.player;

import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import com.dipasquale.search.mcts.SearchResult;
import com.dipasquale.simulation.game2048.GameAction;
import com.dipasquale.simulation.game2048.GameState;
import com.dipasquale.simulation.game2048.Player;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public final class MctsPlayer implements Player {
    private final MonteCarloTreeSearch<GameAction, GameState> mcts;
    private final boolean debug;

    @Override
    public SearchResult<GameAction, GameState> produceNext(final SearchResult<GameAction, GameState> searchResult) {
        return mcts.proposeNext(searchResult);
    }

    @Override
    public void accept(final SearchResult<GameAction, GameState> searchResult) {
        mcts.reset();

        if (debug) {
            searchResult.getState().print(System.out);
        }
    }
}
