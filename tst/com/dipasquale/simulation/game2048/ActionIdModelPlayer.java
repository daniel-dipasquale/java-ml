package com.dipasquale.simulation.game2048;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ActionIdModelPlayer implements Player {
    private final ActionIdModel actionIdModel;

    @Override
    public GameAction createNextAction(final GameState state) {
        int actionId = actionIdModel.getActionId(state);
        ActionIdType actionIdType = ActionIdType.from(actionId);

        return state.createAction(actionIdType);
    }

    @Override
    public void accept(final GameState state) {
        actionIdModel.reset(state);
    }
}
