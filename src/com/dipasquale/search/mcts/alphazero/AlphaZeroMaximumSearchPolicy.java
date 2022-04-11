package com.dipasquale.search.mcts.alphazero;

import com.dipasquale.search.mcts.SearchPolicy;
import lombok.Builder;

@Builder
public final class AlphaZeroMaximumSearchPolicy implements SearchPolicy {
    private final int maximumExpansions;

    @Override
    public void begin() {
    }

    @Override
    public int getMaximumSelectionCount() {
        return maximumExpansions;
    }

    @Override
    public void end() {
    }
}
