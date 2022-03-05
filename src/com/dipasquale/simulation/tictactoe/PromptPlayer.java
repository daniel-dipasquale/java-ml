package com.dipasquale.simulation.tictactoe;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class PromptPlayer implements Player {
    private final ActionPrompter actionPrompter;

    @Override
    public GameAction createNextAction(final GameState state) {
        int actionId = actionPrompter.getActionId();

        return state.createAction(actionId);
    }

    @FunctionalInterface
    public interface ActionPrompter {
        int getActionId();
    }
}
