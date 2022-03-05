package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.Edge;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(AccessLevel.PACKAGE)
public final class AlphaZeroEdge implements Edge {
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.NONE)
    private final AlphaZeroEdge parent;
    @Setter(AccessLevel.NONE)
    private int visited = 0;
    private float expectedReward = 0f;
    private float probableReward = 0f;
    private float explorationProbability = 0f;

    void increaseVisited() {
        visited++;
    }
}
