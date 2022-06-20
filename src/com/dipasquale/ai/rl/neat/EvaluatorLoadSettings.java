package com.dipasquale.ai.rl.neat;

import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
@Getter(AccessLevel.PACKAGE)
public final class EvaluatorLoadSettings {
    private final NeatEnvironment fitnessFunction;
    private final ParallelEventLoop eventLoop;

    Context.StateOverrideSupport createContext() {
        return new ContextObjectStateOverrideSupport(fitnessFunction, eventLoop);
    }
}
