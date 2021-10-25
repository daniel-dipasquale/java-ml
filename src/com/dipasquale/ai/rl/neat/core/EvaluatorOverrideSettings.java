package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.synchronization.event.loop.IterableEventLoop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public final class EvaluatorOverrideSettings {
    private final NeatEnvironment fitnessFunction;
    private final IterableEventLoop eventLoop;
}
