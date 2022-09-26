package com.dipasquale.ai.rl.neat;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
final class DefaultNeatContextParallelismParameters implements NeatContext.ParallelismParameters {
    private final NeatContext.ParallelismParameters parameters;

    @Override
    public boolean isEnabled() {
        return parameters.isEnabled();
    }

    @Override
    public int getNumberOfThreads() {
        return parameters.getNumberOfThreads();
    }
}
