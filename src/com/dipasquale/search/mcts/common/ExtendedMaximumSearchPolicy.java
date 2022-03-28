package com.dipasquale.search.mcts.common;

import lombok.Builder;

@Builder
public final class ExtendedMaximumSearchPolicy implements ExtendedSearchPolicy {
    private final int maximumSelections;
    private final int maximumSimulationRolloutDepth;
    private final Integer perspectiveParticipantId;

    @Override
    public void begin() {
    }

    @Override
    public boolean allowSelection(final int simulations, final int initialDepth) {
        return simulations <= maximumSelections;
    }

    @Override
    public boolean allowSimulationRollout(final int simulations, final int initialDepth, final int currentDepth, final int participantId) {
        return currentDepth - initialDepth <= maximumSimulationRolloutDepth || perspectiveParticipantId != null && perspectiveParticipantId != participantId;
    }

    @Override
    public void end() {
    }
}
