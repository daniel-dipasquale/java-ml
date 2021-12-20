package com.dipasquale.search.mcts;

public interface SimulationPredicate {
    void beginSearch();

    boolean allowSimulation(int simulation);

    boolean allowDepth(int simulation, int depth);
}
