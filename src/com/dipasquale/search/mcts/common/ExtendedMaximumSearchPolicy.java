package com.dipasquale.search.mcts.common;

import lombok.Builder;
import lombok.Getter;

@Builder
public final class ExtendedMaximumSearchPolicy implements ExtendedSearchPolicy {
    @Getter
    private final int maximumSelectionCount;
    private final int maximumSimulationRolloutDepth;

    @Override
    public void begin() {
    }

    @Override
    public boolean allowSimulationRollout(final int simulations, final int initialDepth, final int currentDepth, final int participantId) {
        return currentDepth - initialDepth <= maximumSimulationRolloutDepth;
    }

    @Override
    public void end() {
    }
}
