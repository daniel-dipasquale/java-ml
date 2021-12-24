package com.dipasquale.search.mcts;

import lombok.Builder;

@Builder
public final class DeterministicSimulationPolicy implements SimulationPolicy {
    private final int maximumSimulation;
    private final int maximumDepth;
    private final float abortionRate;

    @Override
    public void beginSearch() {
    }

    private boolean isBelowAbortionRate(final int simulation, final int aborted) {
        if (Float.compare(abortionRate, 1f) == 0) {
            return true;
        }

        return Float.compare((float) aborted / (float) simulation, abortionRate) < 0;
    }

    @Override
    public boolean allowSimulation(final int simulation, final int aborted) {
        return simulation <= maximumSimulation && isBelowAbortionRate(simulation, aborted);
    }

    @Override
    public boolean allowDepth(final int simulation, final int depth) {
        return depth <= maximumDepth;
    }
}
