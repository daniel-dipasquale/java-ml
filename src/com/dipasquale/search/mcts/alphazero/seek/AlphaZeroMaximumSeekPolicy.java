package com.dipasquale.search.mcts.alphazero.seek;

import com.dipasquale.search.mcts.seek.SeekPolicy;
import lombok.Builder;

@Builder
public final class AlphaZeroMaximumSeekPolicy implements SeekPolicy {
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
