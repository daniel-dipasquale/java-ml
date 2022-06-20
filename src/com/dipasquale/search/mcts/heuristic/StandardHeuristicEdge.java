package com.dipasquale.search.mcts.heuristic;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public final class StandardHeuristicEdge implements HeuristicEdge {
    @Getter
    @Setter(AccessLevel.NONE)
    private int visited = 0;
    private float expectedReward = 0f;
    private float probableReward = 0f;
    private float explorationProbability = 1f;

    public StandardHeuristicEdge(final StandardHeuristicEdge edge) {
        this.visited = edge.visited;
        this.expectedReward = edge.expectedReward;
        this.probableReward = edge.probableReward;
        this.explorationProbability = edge.explorationProbability;
    }

    @Override
    public void increaseVisited() {
        visited++;
    }
}
