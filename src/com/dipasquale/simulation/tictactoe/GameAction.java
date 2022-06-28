package com.dipasquale.simulation.tictactoe;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class GameAction {
    private final int locationId;
}
