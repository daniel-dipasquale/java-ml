package com.dipasquale.simulation.tictactoe;

@FunctionalInterface
public interface ActionIdModel {
    int getActionId(GameState state);
}
