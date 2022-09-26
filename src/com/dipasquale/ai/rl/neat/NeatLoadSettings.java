package com.dipasquale.ai.rl.neat;

import com.dipasquale.synchronization.event.loop.ParallelEventLoop;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
@Getter(AccessLevel.PACKAGE)
final class NeatLoadSettings {
    private final NeatEnvironment fitnessFunction;
    private final ParallelEventLoop eventLoop;

    NeatContext.PretrainedSupport createContext() {
        return new DefaultNeatContextPretrainedSupport(fitnessFunction, eventLoop);
    }
}
