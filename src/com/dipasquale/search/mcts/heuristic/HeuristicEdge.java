package com.dipasquale.search.mcts.heuristic;

import com.dipasquale.search.mcts.common.TechniqueEdge;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter
public final class HeuristicEdge implements TechniqueEdge {
    @Getter
    @Setter(AccessLevel.NONE)
    private int visited = 0;
    private float expectedReward = 0f;
    private float probableReward = 0f;
    private float explorationProbability = 1f;

    @Override
    public void increaseVisited() {
        visited++;
    }
}
