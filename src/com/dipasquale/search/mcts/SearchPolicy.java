package com.dipasquale.search.mcts;

public interface SearchPolicy {
    void begin();

    boolean allowSelection(int simulations, int initialDepth);

    void end();
}
