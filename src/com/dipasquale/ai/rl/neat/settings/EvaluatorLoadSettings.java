package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.context.DefaultContextStateOverrideSupport;
import com.dipasquale.ai.rl.neat.core.NeatEnvironment;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class EvaluatorLoadSettings {
    private final NeatEnvironment fitnessFunction;
    private final IterableEventLoop eventLoop;

    public Context.StateOverrideSupport createContext() { // TODO: think of a better fix for this (as a reminder, the fact this is public, is not ideal)
        return new DefaultContextStateOverrideSupport(fitnessFunction, eventLoop);
    }
}
