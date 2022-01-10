package com.dipasquale.ai.rl.neat.core;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
final class ContextObjectParallelismParameters implements Context.ParallelismParameters {
    private final Context.ParallelismParameters parameters;

    @Override
    public boolean enabled() {
        return parameters.enabled();
    }

    @Override
    public int numberOfThreads() {
        return parameters.numberOfThreads();
    }
}
