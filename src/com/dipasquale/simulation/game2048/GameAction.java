package com.dipasquale.simulation.game2048;

import com.dipasquale.search.mcts.Action;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class GameAction implements Action {
    @Getter(AccessLevel.PACKAGE)
    private final String parentCacheId;
    private String cacheId;
    private final int id;
    private List<ValuedTile> valuedTilesAdded;
}
