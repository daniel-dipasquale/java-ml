package com.dipasquale.search.mcts.core;

import lombok.Builder;

@Builder
public final class DeterministicSearchPolicy implements SearchPolicy {
    private final int maximumSimulations;
    private final int maximumDepth;

    @Override
    public void begin() {
    }

    @Override
    public boolean allowSimulation(final int simulation) {
        return simulation <= maximumSimulations;
    }

    @Override
    public boolean allowDepth(final int simulation, final int depth) {
        return depth <= maximumDepth;
    }

    @Override
    public void end() {
    }
}