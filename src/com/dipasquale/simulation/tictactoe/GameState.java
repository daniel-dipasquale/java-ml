package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.core.State;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class GameState implements State {
    @Getter(AccessLevel.PACKAGE)
    private final Object membership;
    @Getter
    private final int participantId;
    @Getter
    private final int location;
}
