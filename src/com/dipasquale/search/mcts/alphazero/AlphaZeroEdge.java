package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.common.TechniqueEdge;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter
public final class AlphaZeroEdge implements TechniqueEdge {
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
