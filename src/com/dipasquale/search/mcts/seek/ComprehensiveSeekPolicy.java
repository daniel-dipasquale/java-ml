package com.dipasquale.search.mcts.seek;

public interface ComprehensiveSeekPolicy extends SeekPolicy {
    boolean allowSimulation(int simulations, int initialDepth, int currentDepth, int participantId);
}
