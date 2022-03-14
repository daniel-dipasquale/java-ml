package com.dipasquale.simulation.tictactoe;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@ToString
public final class GameResult {
    public static final int DRAWN_OUTCOME_ID = -1;
    private final int outcomeId;
    private final int[] actionIds;
}
