package com.dipasquale.simulation.game2048;

import com.dipasquale.search.mcts.SearchResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ActionIdModelPlayer implements Player {
    private final ActionIdModel actionIdModel;

    @Override
    public SearchResult<GameAction, GameState> produceNext(final SearchResult<GameAction, GameState> searchResult) {
        GameState state = searchResult.getState();
        int actionId = actionIdModel.getActionId(state);
        ActionIdType actionIdType = ActionIdType.from(actionId);
        GameAction action = state.createAction(actionIdType);

        return searchResult.createChild(action);
    }

    @Override
    public void accept(final SearchResult<GameAction, GameState> searchResult) {
        actionIdModel.reset(searchResult.getState());
    }
}
