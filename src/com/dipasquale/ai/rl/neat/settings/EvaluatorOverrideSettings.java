package com.dipasquale.ai.rl.neat.settings;

import com.dipasquale.ai.rl.neat.core.NeatEnvironment;
import com.dipasquale.threading.event.loop.IterableEventLoop;
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
