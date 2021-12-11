package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.common.RandomType;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public final class InitializationContext {
    private final NeatEnvironmentType neatEnvironmentType;
    private final ParallelismSupport parallelism;
    private final Map<RandomType, DualModeRandomSupport> randomSupports;
    private final DualModeRandomSupport randomSupport;
    private final SingletonContainer container = new SingletonContainer();
}
