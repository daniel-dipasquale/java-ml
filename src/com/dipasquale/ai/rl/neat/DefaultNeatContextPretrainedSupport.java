package com.dipasquale.ai.rl.neat;

import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class DefaultNeatContextPretrainedSupport implements NeatContext.PretrainedSupport {
    private final NeatEnvironment fitnessFunction;
    private final ParallelEventLoop eventLoop;

    @Override
    public NeatEnvironment getFitnessFunction() {
        return fitnessFunction;
    }

    @Override
    public ParallelEventLoop getEventLoop() {
        return eventLoop;
    }
}
