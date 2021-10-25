package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.context.DefaultContextStateOverrideSupport;
import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
@Getter(AccessLevel.PACKAGE)
public final class EvaluatorLoadSettings {
    private final NeatEnvironment fitnessFunction;
    private final IterableEventLoop eventLoop;

    Context.StateOverrideSupport createContext() {
        return new DefaultContextStateOverrideSupport(fitnessFunction, eventLoop);
    }
}
