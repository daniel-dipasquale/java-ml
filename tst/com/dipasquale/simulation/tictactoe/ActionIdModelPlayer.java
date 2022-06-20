package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.SearchResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ActionIdModelPlayer implements Player {
    private final ActionIdModel actionIdModel;

    @Override
    public SearchResult<GameAction, GameState> produceNext(final SearchResult<GameAction, GameState> searchResult) {
        int actionId = actionIdModel.getActionId(searchResult.getState());
        GameAction action = searchResult.getState().createAction(actionId);

        return searchResult.createChild(action);
    }

    @Override
    public void accept(final SearchResult<GameAction, GameState> searchResult) {
        actionIdModel.reset(searchResult.getState());
    }
}
