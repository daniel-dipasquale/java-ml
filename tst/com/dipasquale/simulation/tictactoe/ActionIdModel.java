package com.dipasquale.simulation.tictactoe;

public interface ActionIdModel {
    int getActionId(GameState state);

    void reset(GameState state);
}
