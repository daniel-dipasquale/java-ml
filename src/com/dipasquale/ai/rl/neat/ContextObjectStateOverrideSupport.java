package com.dipasquale.ai.rl.neat;

import com.dipasquale.synchronization.event.loop.BatchingEventLoop;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ContextObjectStateOverrideSupport implements Context.StateOverrideSupport {
    private final NeatEnvironment fitnessFunction;
    private final BatchingEventLoop eventLoop;

    @Override
    public NeatEnvironment fitnessFunction() {
        return fitnessFunction;
    }

    @Override
    public BatchingEventLoop eventLoop() {
        return eventLoop;
    }
}
