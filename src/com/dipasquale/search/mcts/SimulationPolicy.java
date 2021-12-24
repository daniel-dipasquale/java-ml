package com.dipasquale.search.mcts;

public interface SimulationPolicy {
    void beginSearch();

    boolean allowSimulation(int simulation, int aborted);

    boolean allowDepth(int simulation, int depth);
}