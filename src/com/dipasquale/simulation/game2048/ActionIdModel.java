package com.dipasquale.simulation.game2048;

@FunctionalInterface
public interface ActionIdModel {
    int getActionId(GameState state);
}
