package com.dipasquale.simulation.game2048;

public interface Player {
    void initializeState(GameState state);

    GameAction createNextAction(GameState state);
}
