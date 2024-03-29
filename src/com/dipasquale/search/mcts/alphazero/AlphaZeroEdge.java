package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.heuristic.HeuristicEdge;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter
public final class AlphaZeroEdge implements HeuristicEdge {
    @Getter
    @Setter(AccessLevel.NONE)
    private int visited = 0;
    private float expectedReward = 0f;
    private float probableReward = 0f;
    private float explorationProbability = 0f;

    @Override
    public void increaseVisited() {
        visited++;
    }
}
