package com.dipasquale.search.mcts.classic;

import com.dipasquale.search.mcts.SearchPolicy;

public interface ClassicSearchPolicy extends SearchPolicy {
    boolean allowSimulationRollout(int simulations, int initialDepth, int currentDepth);
}
