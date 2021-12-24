package com.dipasquale.ai.rl.neat.core;

import com.dipasquale.ai.rl.neat.synchronization.dual.mode.factory.DualModeRandomSupportFactory;
import com.dipasquale.synchronization.dual.mode.data.structure.deque.DualModeDequeFactory;
import com.dipasquale.synchronization.dual.mode.data.structure.map.DualModeMapFactory;
import com.dipasquale.synchronization.dual.mode.random.float1.DualModeRandomSupport;
import lombok.Getter;

public final class InitializationContext {
    private static final DualModeRandomSupportFactory RANDOM_SUPPORT_FACTORY = DualModeRandomSupportFactory.getInstance();
    @Getter
    private final NeatEnvironmentType environmentType;
    private final ParallelismSupport parallelism;
    @Getter
    private final DualModeMapFactory mapFactory;
    @Getter
    private final DualModeDequeFactory dequeFactory;
    private final RandomSupport random;
    @Getter
    private final SingletonContainer container = new SingletonContainer();

    InitializationContext(final NeatEnvironmentType environmentType, final ParallelismSupport parallelism, final RandomSupport random) {
        int concurrencyLevel = parallelism.getConcurrencyLevel();
        int maximumConcurrencyLevel = getMaximumConcurrencyLevel(concurrencyLevel);

        this.environmentType = environmentType;
        this.parallelism = parallelism;
        this.mapFactory = new DualModeMapFactory(concurrencyLevel, maximumConcurrencyLevel);
        this.dequeFactory = new DualModeDequeFactory(concurrencyLevel, maximumConcurrencyLevel);
        this.random = random;
    }

    public int getConcurrencyLevel() {
        return parallelism.getConcurrencyLevel();
    }

    private static int getMaximumConcurrencyLevel(final int concurrencyLevel) {
        int availableProcessors = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);

        return Math.max(concurrencyLevel, availableProcessors);
    }

    public DualModeRandomSupport createRandomSupport(final RandomType randomType) {
        return RANDOM_SUPPORT_FACTORY.create(getConcurrencyLevel(), randomType);
    }

    public DualModeRandomSupport createDefaultRandomSupport() {
        return createRandomSupport(random.getType());
    }
}
