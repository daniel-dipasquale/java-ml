package com.dipasquale.simulation.tictactoe;

public interface Player {
    GameAction createNextAction(GameState state);

    void accept(GameResult result);
}
