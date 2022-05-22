package com.dipasquale.search.mcts.common;

import com.dipasquale.search.mcts.SearchPolicy;

public interface FullSearchPolicy extends SearchPolicy {
    boolean allowSimulationRollout(int simulations, int initialDepth, int currentDepth, int participantId);
}
