package com.dipasquale.simulation.tictactoe;

@FunctionalInterface
public interface Player {
    GameAction createNextAction(GameState state);
}
