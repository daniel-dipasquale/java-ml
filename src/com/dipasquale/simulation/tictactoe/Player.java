package com.dipasquale.simulation.tictactoe;

@FunctionalInterface
public interface Player {
    GameAction createNextState(GameState environment);
}
