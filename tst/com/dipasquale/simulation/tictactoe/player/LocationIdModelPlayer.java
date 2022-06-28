package com.dipasquale.simulation.tictactoe.player;

import com.dipasquale.search.mcts.SearchResult;
import com.dipasquale.simulation.tictactoe.GameAction;
import com.dipasquale.simulation.tictactoe.GameState;
import com.dipasquale.simulation.tictactoe.Player;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class LocationIdModelPlayer implements Player {
    private final LocationIdModel locationIdModel;

    @Override
    public SearchResult<GameAction, GameState> produceNext(final SearchResult<GameAction, GameState> searchResult) {
        GameState state = searchResult.getState();
        int locationId = locationIdModel.produceNext(state);
        GameAction action = state.createAction(locationId);

        return searchResult.createChild(locationId, action);
    }

    @Override
    public void accept(final SearchResult<GameAction, GameState> searchResult) {
        locationIdModel.restart();
    }
}
