package com.dipasquale.simulation.game2048.player;

import com.dipasquale.search.mcts.SearchResult;
import com.dipasquale.simulation.game2048.GameAction;
import com.dipasquale.simulation.game2048.GameState;
import com.dipasquale.simulation.game2048.Player;
import com.dipasquale.simulation.game2048.ValuedTile;
import com.dipasquale.simulation.game2048.ValuedTileSupport;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ValuedTileAllocationPlayer implements Player {
    private final ValuedTileSupport valuedTileSupport;

    private GameAction generateInitialAction(final GameState state) {
        int tileId1 = valuedTileSupport.generateFreedTileId(0);
        int tileId2 = valuedTileSupport.generateFreedTileId(1);
        ValuedTile valuedTile1 = new ValuedTile(tileId1, valuedTileSupport.generateExponentialValue());
        ValuedTile valuedTile2;

        if (tileId2 >= tileId1) {
            valuedTile2 = new ValuedTile(tileId2 + 1, valuedTileSupport.generateExponentialValue());
        } else {
            valuedTile2 = new ValuedTile(tileId2, valuedTileSupport.generateExponentialValue());
        }

        return state.createInitialAction(valuedTile1, valuedTile2);
    }

    private GameAction generateValuedTileAllocationAction(final GameState state) {
        int freedTileId = valuedTileSupport.generateFreedTileId(state.getValuedTileCount());
        int tileId = state.getTileId(freedTileId);
        int exponentialValue = valuedTileSupport.generateExponentialValue();

        return state.createValuedTileAllocationAction(new ValuedTile(tileId, exponentialValue));
    }

    @Override
    public SearchResult<GameAction, GameState> produceNext(final SearchResult<GameAction, GameState> searchResult) {
        GameAction action;

        if (searchResult.getStateId().getDepth() == 0) {
            action = generateInitialAction(searchResult.getState());
        } else {
            action = generateValuedTileAllocationAction(searchResult.getState());
        }

        return searchResult.createChild(action.getId(), action);
    }

    @Override
    public void accept(final SearchResult<GameAction, GameState> searchResult) {
    }
}
