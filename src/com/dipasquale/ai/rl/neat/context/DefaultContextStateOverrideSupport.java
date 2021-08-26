package com.dipasquale.ai.rl.neat.context;

import com.dipasquale.ai.rl.neat.core.NeatEnvironment;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class DefaultContextStateOverrideSupport implements Context.StateOverrideSupport {
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
