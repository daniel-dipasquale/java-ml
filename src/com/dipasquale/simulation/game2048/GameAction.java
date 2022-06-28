package com.dipasquale.simulation.game2048;

import com.dipasquale.search.mcts.MonteCarloTreeSearch;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode
@ToString
public final class GameAction {
    private final int id;
    private List<ValuedTile> valuedTilesAdded;

    static GameAction createRoot() {
        return new GameAction(MonteCarloTreeSearch.ROOT_ACTION_ID, ValuedTileSupport.EMPTY_VALUED_TILES);
    }

    static GameAction createIntentional(final int id) {
        return new GameAction(id, ValuedTileSupport.EMPTY_VALUED_TILES);
    }

    static GameAction createIntentional(final ActionIdType idType) {
        return createIntentional(idType.getValue());
    }
}
