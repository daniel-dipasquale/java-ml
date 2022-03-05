package com.dipasquale.ai.rl.neat;

import com.dipasquale.synchronization.event.loop.BatchingEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
@Getter(AccessLevel.PACKAGE)
public final class EvaluatorLoadSettings {
    private final NeatEnvironment fitnessFunction;
    private final BatchingEventLoop eventLoop;

    Context.StateOverrideSupport createContext() {
        return new ContextObjectStateOverrideSupport(fitnessFunction, eventLoop);
    }
}
