package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.SearchNodeResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ActionIdModelPlayer implements Player {
    private final ActionIdModel actionIdModel;

    @Override
    public SearchNodeResult<GameAction, GameState> produceNext(final SearchNodeResult<GameAction, GameState> searchNodeResult) {
        int actionId = actionIdModel.getActionId(searchNodeResult.getState());
        GameAction action = searchNodeResult.getState().createAction(actionId);

        return searchNodeResult.createChild(action);
    }

    @Override
    public void accept(final SearchNodeResult<GameAction, GameState> searchNodeResult) {
        actionIdModel.reset(searchNodeResult.getState());
    }
}
