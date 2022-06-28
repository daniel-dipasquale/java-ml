package com.dipasquale.simulation.game2048.player;

import com.dipasquale.simulation.game2048.GameState;

public interface ActionIdModel {
    int getActionId(GameState state);

    void reset(GameState state);
}
