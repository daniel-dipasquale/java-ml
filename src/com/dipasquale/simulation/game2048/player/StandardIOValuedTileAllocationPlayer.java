package com.dipasquale.simulation.game2048.player;

import com.dipasquale.common.factory.ObjectFactory;
import com.dipasquale.search.mcts.SearchResult;
import com.dipasquale.simulation.game2048.ActionIdType;
import com.dipasquale.simulation.game2048.GameAction;
import com.dipasquale.simulation.game2048.GameState;
import com.dipasquale.simulation.game2048.Player;
import com.dipasquale.simulation.game2048.ValuedTile;

import java.util.List;

public final class StandardIOValuedTileAllocationPlayer implements Player {
    private final ObjectFactory<GameStandardIOClient> clientFactory;
    private GameStandardIOClient client;

    public StandardIOValuedTileAllocationPlayer(final ObjectFactory<GameStandardIOClient> clientFactory) { // TODO: enrich the client API so that a factory isn't required
        this.clientFactory = clientFactory;
        this.client = clientFactory.create();
    }

    private ValuedTile replicatePlayerAction(final GameAction lastAction) {
        ActionIdType actionIdType = ActionIdType.from(lastAction.getId());

        return client.move(actionIdType);
    }

    @Override
    public SearchResult<GameAction, GameState> produceNext(final SearchResult<GameAction, GameState> searchResult) {
        if (searchResult.getStateId().getDepth() == 0) {
            List<ValuedTile> valuedTiles = client.start();
            GameAction action = searchResult.getState().createInitialAction(valuedTiles.get(0), valuedTiles.get(1));

            return searchResult.createChild(action.getId(), action);
        }

        ValuedTile valuedTile = replicatePlayerAction(searchResult.getAction());
        GameAction action = searchResult.getState().createValuedTileAllocationAction(valuedTile);

        return searchResult.createChild(action.getId(), action);
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
            client = clientFactory.create();
        }
    }
}
