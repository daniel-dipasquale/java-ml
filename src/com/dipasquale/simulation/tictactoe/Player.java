package com.dipasquale.simulation.tictactoe;

@FunctionalInterface
public interface Player {
    GameState createNextState(GameEnvironment environment);
}
