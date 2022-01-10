package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.core.Edge;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class AlphaZeroEdge implements Edge {
    private int visited = 0;
    @Setter(AccessLevel.PACKAGE)
    private float expectedReward = 0f;
    @Setter(AccessLevel.PACKAGE)
    private float probableReward = 0f;
    @Setter(AccessLevel.PACKAGE)
    private float explorationProbability = 0f;

    void increaseVisited() {
        visited++;
    }
}
