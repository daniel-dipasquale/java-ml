package com.dipasquale.search.mcts;

public interface Edge {
    int getVisited();

    float getExplorationProbability();

    void setExplorationProbability(float value);
}
