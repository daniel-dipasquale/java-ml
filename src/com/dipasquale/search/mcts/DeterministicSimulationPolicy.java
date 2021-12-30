package com.dipasquale.search.mcts;

import lombok.Builder;

@Builder
public final class DeterministicSimulationPolicy implements SimulationPolicy {
    private final int maximumSimulation;
    private final int maximumDepth;

    @Override
    public void beginSearch() {
    }

    @Override
    public boolean allowSimulation(final int simulation) {
        return simulation <= maximumSimulation;
    }

    @Override
    public boolean allowDepth(final int simulation, final int depth) {
        return depth <= maximumDepth;
    }
}
