package com.dipasquale.simulation.game2048;

import com.dipasquale.search.mcts.Action;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class GameAction implements Action {
    @Getter(AccessLevel.PACKAGE)
    private final String parentCacheId;
    @Setter(AccessLevel.PACKAGE)
    private String cacheId;
    private final int id;
    private final int participantId;
    @Setter(AccessLevel.PACKAGE)
    private ValuedTile valuedTileAdded;
}
