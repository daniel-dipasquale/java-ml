package com.dipasquale.search.mcts.core;

import lombok.Builder;

@Builder
public final class DeterministicSimulationPolicy implements SimulationPolicy {
    private final int maximumSimulations;
    private final int maximumDepth;

    @Override
    public void beginSearch() {
    }

    @Override
    public boolean allowSimulation(final int simulation) {
        return simulation <= maximumSimulations;
    }

    @Override
    public boolean allowDepth(final int simulation, final int depth) {
        return depth <= maximumDepth;
    }
}
