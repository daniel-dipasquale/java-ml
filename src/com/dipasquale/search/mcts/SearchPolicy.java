package com.dipasquale.search.mcts;

public interface SearchPolicy {
    void begin();

    int getMaximumSelectionCount();

    void end();
}
