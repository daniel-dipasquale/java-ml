package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.core.SearchEdge;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
public final class AlphaZeroSearchEdge implements SearchEdge {
    @Setter(AccessLevel.PACKAGE)
    private float expectedReward;
    @Setter(AccessLevel.PACKAGE)
    private float probableReward;
    private int visited;
    @Setter(AccessLevel.PACKAGE)
    private float explorationProbability;

    AlphaZeroSearchEdge() {
        this.visited = 0;
    }

    AlphaZeroSearchEdge(final AlphaZeroSearchEdge parent) {
        this.visited = parent.visited;
    }

    void increaseVisited() {
        visited++;
    }
}
