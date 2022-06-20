package com.dipasquale.simulation.game2048;

import com.dipasquale.search.mcts.SearchResult;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public final class StandardIOValuedTileAdderPlayer implements Player {
    private GameStandardIOClient client = new GameStandardIOClient();

    private ValuedTile replicatePlayerAction(final GameAction lastAction) {
        ActionIdType actionIdType = ActionIdType.from(lastAction.getId());

        return client.move(actionIdType);
    }

    @Override
    public SearchResult<GameAction, GameState> produceNext(final SearchResult<GameAction, GameState> searchResult) {
        GameState state = searchResult.getState();

        if (state.getDepth() == 0) {
            List<ValuedTile> valuedTiles = client.start();
            GameAction action = state.createInitialAction(valuedTiles.get(0), valuedTiles.get(1));

            return searchResult.createChild(action);
        }

        ValuedTile valuedTile = replicatePlayerAction(searchResult.getAction());
        GameAction action = state.createActionToAddValuedTile(valuedTile);

        return searchResult.createChild(action);
    }

    @Override
    public void accept(final SearchResult<GameAction, GameState> searchResult) {
        if (searchResult.getState().getParticipantId() == 2) {
            replicatePlayerAction(searchResult.getAction());
        }

        try {
            client.close();
        } catch (Exception e) {
            throw new RuntimeException("unable to close client", e);
        } finally {
            client = new GameStandardIOClient();
        }
    }
}
