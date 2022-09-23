package com.dipasquale.ai.rl.neat;

import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ContextObjectLoadSupport implements Context.LoadSupport {
    private final NeatEnvironment fitnessFunction;
    private final ParallelEventLoop eventLoop;

    @Override
    public NeatEnvironment fitnessFunction() {
        return fitnessFunction;
    }

    @Override
    public ParallelEventLoop eventLoop() {
        return eventLoop;
    }
}
