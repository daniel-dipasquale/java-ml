package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class ContextObjectStateOverrideSupport implements Context.StateOverrideSupport {
    private final NeatEnvironment fitnessFunction;
    private final IterableEventLoop eventLoop;

    @Override
    public NeatEnvironment fitnessFunction() {
        return fitnessFunction;
    }

    @Override
    public IterableEventLoop eventLoop() {
        return eventLoop;
    }
}