package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.Edge;

public interface TechniqueEdge extends Edge {
    void increaseVisited();

    float getExpectedReward();

    void setExpectedReward(float value);

    float getProbableReward();

    void setProbableReward(float value);
}