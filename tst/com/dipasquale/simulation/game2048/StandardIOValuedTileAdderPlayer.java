package com.dipasquale.simulation.game2048;

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
    public GameAction createNextAction(final GameState state) {
        if (state.getDepth() == 0) {
            List<ValuedTile> valuedTiles = client.start();

            return state.createInitialAction(valuedTiles.get(0), valuedTiles.get(1));
        }

        ValuedTile valuedTile = replicatePlayerAction(state.getLastAction());

        return state.createActionToAddValuedTile(valuedTile);
    }

    @Override
    public void accept(final GameState state) {
        if (state.getParticipantId() == 2) {
            replicatePlayerAction(state.getLastAction());
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
