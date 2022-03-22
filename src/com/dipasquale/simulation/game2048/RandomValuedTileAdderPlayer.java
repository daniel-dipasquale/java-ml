package com.dipasquale.simulation.game2048;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class RandomValuedTileAdderPlayer implements Player {
    @Override
    public GameAction createNextAction(final GameState state) {
        if (state.getDepth() == 0) {
            return state.generateInitialAction();
        }

        return state.generateActionToAddValuedTile();
    }

    @Override
    public void accept(final GameResult result) {
    }
}
