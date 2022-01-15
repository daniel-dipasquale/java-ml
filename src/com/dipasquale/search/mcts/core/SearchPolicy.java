package com.dipasquale.search.mcts.core;

public interface SearchPolicy {
    void begin();

    boolean allowSimulation(int simulation);

    boolean allowDepth(int simulation, int depth, int simulatedDepth);

    void end();
}
