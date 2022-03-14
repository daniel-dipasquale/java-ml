package com.dipasquale.search.mcts.classic;

import lombok.Builder;

@Builder
public final class ClassicMaximumSearchPolicy implements ClassicSearchPolicy {
    private final int maximumSelections;
    private final int maximumSimulationRolloutDepth;

    @Override
    public void begin() {
    }

    @Override
    public boolean allowSelection(final int simulations, final int initialDepth) {
        return simulations <= maximumSelections;
    }

    @Override
    public boolean allowSimulationRollout(final int simulations, final int initialDepth, final int currentDepth) {
        return currentDepth - initialDepth <= maximumSimulationRolloutDepth;
    }

    @Override
    public void end() {
    }
}
