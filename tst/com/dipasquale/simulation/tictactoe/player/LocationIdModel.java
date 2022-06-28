package com.dipasquale.simulation.tictactoe.player;

import com.dipasquale.simulation.tictactoe.GameState;

public interface LocationIdModel {
    int produceNext(GameState state);

    void restart();
}
