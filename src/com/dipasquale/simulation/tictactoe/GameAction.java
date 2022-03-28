package com.dipasquale.simulation.tictactoe;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.TreeId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class GameAction implements Action {
    private final TreeId treeId;
    private final int id;
}
