package com.dipasquale.simulation.game2048;

public interface Player {
    GameAction createNextAction(GameState state);

    void accept(GameResult result);
}
