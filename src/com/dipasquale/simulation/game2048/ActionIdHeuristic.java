package com.dipasquale.simulation.game2048;

@FunctionalInterface
public interface ActionIdHeuristic {
    int getActionId(GameState state);
}
