package com.dipasquale.simulation.tictactoe;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ActionIdModelPlayer implements Player {
    private final ActionIdModel actionIdModel;

    @Override
    public GameAction createNextAction(final GameState state) {
        int actionId = actionIdModel.getActionId(state);

        return state.createAction(actionId);
    }

    @Override
    public void accept(final GameResult result) {
        actionIdModel.reset(result);
    }
}
