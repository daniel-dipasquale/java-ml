package com.dipasquale.simulation.game2048;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class RandomOutcomePlayer implements Player {
    private final ActionIdHeuristic actionIdHeuristic;

    @Override
    public void initializeState(final GameState state) {
        state.initializeRandomTiles();
    }

    @Override
    public GameAction createNextAction(final GameState state) {
        int actionId = actionIdHeuristic.getActionId(state);

        return state.createRandomOutcomeAction(actionId);
    }
}
