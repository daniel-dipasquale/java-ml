package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Edge;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class AlphaZeroEdge implements Edge {
    @Getter
    private int visited = 0;
    @Getter
    @Setter(AccessLevel.PACKAGE)
    private float expectedReward = 0f;
    @Getter
    @Setter(AccessLevel.PACKAGE)
    private float probableReward = 0f;
    @Getter
    @Setter(AccessLevel.PACKAGE)
    private float explorationProbability = 0f;

    void increaseVisited() {
        visited++;
    }
}
