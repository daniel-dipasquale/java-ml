package com.dipasquale.ai.rl.neat;

import com.dipasquale.synchronization.event.loop.BatchingEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class EvaluatorOverrideSettings {
    private final NeatEnvironment fitnessFunction;
    private final BatchingEventLoop eventLoop;
}
