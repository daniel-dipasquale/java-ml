package com.dipasquale.simulation.game2048;

import com.dipasquale.search.mcts.Action;
import com.dipasquale.search.mcts.buffer.TreeId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class GameAction implements Action {
    private TreeId treeId;
    private final int id;
    private List<ValuedTile> valuedTilesAdded;
}
