package com.dipasquale.simulation.game2048.player;

import com.dipasquale.search.mcts.SearchResult;
import com.dipasquale.simulation.game2048.ActionIdType;
import com.dipasquale.simulation.game2048.GameAction;
import com.dipasquale.simulation.game2048.GameState;
import com.dipasquale.simulation.game2048.Player;
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

        return searchResult.createChild(actionId, action);
    }

    @Override
    public void accept(final SearchResult<GameAction, GameState> searchResult) {
        actionIdModel.reset(searchResult.getState());
    }
}
