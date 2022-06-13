package com.dipasquale.simulation.game2048;

import com.dipasquale.search.mcts.SearchNodeResult;
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
    public SearchNodeResult<GameAction, GameState> produceNext(final SearchNodeResult<GameAction, GameState> searchNodeResult) {
        GameState state = searchNodeResult.getState();

        if (state.getDepth() == 0) {
            List<ValuedTile> valuedTiles = client.start();
            GameAction action = state.createInitialAction(valuedTiles.get(0), valuedTiles.get(1));

            return searchNodeResult.createChild(action);
        }

        ValuedTile valuedTile = replicatePlayerAction(searchNodeResult.getAction());
        GameAction action = state.createActionToAddValuedTile(valuedTile);

        return searchNodeResult.createChild(action);
    }

    @Override
    public void accept(final SearchNodeResult<GameAction, GameState> searchNodeResult) {
        if (searchNodeResult.getState().getParticipantId() == 2) {
            replicatePlayerAction(searchNodeResult.getAction());
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
