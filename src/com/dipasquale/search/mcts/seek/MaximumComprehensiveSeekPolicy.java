package com.dipasquale.search.mcts.seek;

import lombok.Builder;
import lombok.Getter;

@Builder
public final class MaximumComprehensiveSeekPolicy implements ComprehensiveSeekPolicy {
    @Getter
    private final int maximumSelectionCount;
    private final int maximumSimulationDepth;

    @Override
    public void begin() {
    }

    @Override
    public boolean allowSimulation(final int simulations, final int initialDepth, final int currentDepth, final int participantId) {
        return currentDepth - initialDepth <= maximumSimulationDepth;
    }

    @Override
    public void end() {
    }
}
