package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.core.SearchState;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class GameState implements SearchState {
    @Getter(AccessLevel.PACKAGE)
    private final Object membership;
    @Getter
    private final int participantId;
    @Getter
    private final int location;
}
