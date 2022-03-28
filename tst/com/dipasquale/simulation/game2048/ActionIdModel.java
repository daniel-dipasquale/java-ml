package com.dipasquale.simulation.game2048;

public interface ActionIdModel {
    int getActionId(GameState state);

    void reset(GameState state);
}
