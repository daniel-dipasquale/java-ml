package com.dipasquale.simulation.game2048;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ValuedTileAdderPlayer implements Player {
    private final ValuedTileSupport valuedTileSupport;

    private static int generateValue(final ValuedTileSupport valuedTileSupport) {
        return valuedTileSupport.generateValue(GameState.PROBABILITY_OF_SPAWNING_2);
    }

    private GameAction generateInitialAction(final GameState state) {
        int tileId1 = valuedTileSupport.generateId(0, Board.SQUARE_LENGTH);
        int tileId2 = valuedTileSupport.generateId(0, Board.SQUARE_LENGTH - 1);
        ValuedTile valuedTile1 = new ValuedTile(tileId1, generateValue(valuedTileSupport));
        ValuedTile valuedTile2;

        if (tileId2 >= tileId1) {
            valuedTile2 = new ValuedTile(tileId2 + 1, generateValue(valuedTileSupport));
        } else {
            valuedTile2 = new ValuedTile(tileId2, generateValue(valuedTileSupport));
        }

        return state.createInitialAction(valuedTile1, valuedTile2);
    }

    private GameAction generateActionToAddValuedTile(final GameState state) {
        int tileIdLogical = valuedTileSupport.generateId(0, Board.SQUARE_LENGTH - state.getValuedTileCount());
        int tileId = -1;

        for (int i1 = 0, i2 = 0; tileId == -1; i1++) {
            if (state.getValueInTile(i1) == Board.EMPTY_TILE_VALUE) {
                if (i2++ == tileIdLogical) {
                    tileId = i1;
                }
            }
        }

        int value = generateValue(valuedTileSupport);

        return state.createActionToAddValuedTile(new ValuedTile(tileId, value));
    }

    @Override
    public GameAction createNextAction(final GameState state) {
        if (state.getDepth() == 0) {
            return generateInitialAction(state);
        }

        return generateActionToAddValuedTile(state);
    }

    @Override
    public void accept(final GameState state) {
    }
}
