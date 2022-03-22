package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.Action;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class GameAction implements Action {
    @Getter(AccessLevel.PACKAGE)
    private final String parentCacheId;
    private final String cacheId;
    private final int id;
}
