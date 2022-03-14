package com.dipasquale.search.mcts.alphazero;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
public final class TemperatureController {
    private final int depthThreshold;

    public boolean shouldExplore(final int depth) {
        return depth <= depthThreshold;
    }
}
