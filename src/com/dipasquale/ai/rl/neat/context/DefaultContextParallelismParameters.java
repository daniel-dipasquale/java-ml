package com.dipasquale.ai.rl.neat.context;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class DefaultContextParallelismParameters implements Context.ParallelismParameters {
    private final Context.ParallelismParameters parameters;

    @Override
    public boolean isEnabled() {
        return parameters.isEnabled();
    }

    @Override
    public int numberOfThreads() {
        return parameters.numberOfThreads();
    }
}
