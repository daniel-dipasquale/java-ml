package com.dipasquale.simulation.game2048;

import com.dipasquale.search.mcts.SearchNodeResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ActionIdModelPlayer implements Player {
    private final ActionIdModel actionIdModel;

    @Override
    public SearchNodeResult<GameAction, GameState> produceNext(final SearchNodeResult<GameAction, GameState> searchNodeResult) {
        GameState state = searchNodeResult.getState();
        int actionId = actionIdModel.getActionId(state);
        ActionIdType actionIdType = ActionIdType.from(actionId);
        GameAction action = state.createAction(actionIdType);

        return searchNodeResult.createChild(action);
    }

    @Override
    public void accept(final SearchNodeResult<GameAction, GameState> searchNodeResult) {
        actionIdModel.reset(searchNodeResult.getState());
    }
}
