package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.Edge;

public interface TechniqueEdge extends Edge {
    float MAXIMUM_PROBABLE_REWARD = 1f;

    void increaseVisited();

    float getExpectedReward();

    void setExpectedReward(float value);

    default float calculateExpectedReward() {
        int visited = getVisited();

        if (visited == 0) {
            return -MAXIMUM_PROBABLE_REWARD;
        }

        return getExpectedReward() / (float) visited;
    }

    float getProbableReward();

    void setProbableReward(float value);
}