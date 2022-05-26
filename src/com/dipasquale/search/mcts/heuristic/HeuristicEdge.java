package com.dipasquale.search.mcts.heuristic;

import com.dipasquale.search.mcts.Edge;

public interface HeuristicEdge extends Edge {
    float MAXIMUM_PROBABLE_REWARD = 1f;

    void increaseVisited();

    float getExpectedReward();

    void setExpectedReward(float value);

    default float calculateExpectedReward(final int additionalVisits, final float additionalReward) {
        int visited = getVisited() + additionalVisits;

        if (visited == 0) {
            return -MAXIMUM_PROBABLE_REWARD;
        }

        return (getExpectedReward() + additionalReward) / (float) visited;
    }

    default float calculateExpectedReward() {
        return calculateExpectedReward(0, 0f);
    }

    float getProbableReward();

    void setProbableReward(float value);
}