package com.dipasquale.search.mcts.seek;

public interface FullSeekPolicy extends SeekPolicy {
    boolean allowSimulationRollout(int simulations, int initialDepth, int currentDepth, int participantId);
}
