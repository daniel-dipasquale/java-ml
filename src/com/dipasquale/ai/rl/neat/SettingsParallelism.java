package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.common.SequentialIdFactory;
import com.dipasquale.ai.common.SequentialIdFactoryStrategy;
import com.dipasquale.ai.common.SequentialIdFactoryStrategySynchronized;
import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.context.ContextDefaultParallelism;
import com.dipasquale.common.RandomSupportFloat;
import com.dipasquale.threading.event.loop.EventLoopIterable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SettingsParallelism {
    private static final RandomSupportFloat RANDOM_SUPPORT_UNIFORM = RandomSupportFloat.create();
    private static final RandomSupportFloat RANDOM_SUPPORT_MEAN_DISTRIBUTED = RandomSupportFloat.createMeanDistribution();
    private static final RandomSupportFloat RANDOM_SUPPORT_UNIFORM_CONCURRENT = RandomSupportFloat.createConcurrent();
    private static final RandomSupportFloat RANDOM_SUPPORT_MEAN_DISTRIBUTED_CONCURRENT = RandomSupportFloat.createMeanDistributionConcurrent();
    @Builder.Default
    private final EventLoopIterable eventLoopIterable = null;

    boolean isEnabled() {
        return eventLoopIterable != null;
    }

    int getNumberOfThreads() {
        if (eventLoopIterable == null) {
            return 1;
        }

        return eventLoopIterable.getConcurrencyLevel();
    }

    ContextDefaultParallelism create() {
        if (!isEnabled()) {
            Context.Parallelism parallelism = new ContextDefaultParallelism.SingleThread();

            return new ContextDefaultParallelism(parallelism);
        }

        Context.Parallelism parallelism = new ContextDefaultParallelism.MultiThread(eventLoopIterable);

        return new ContextDefaultParallelism(parallelism);
    }

    RandomSupportFloat getRandomSupport(final SettingsRandomType type) {
        if (!isEnabled()) {
            return switch (type) {
                case UNIFORM -> RANDOM_SUPPORT_UNIFORM;

                case MEAN_DISTRIBUTED -> RANDOM_SUPPORT_MEAN_DISTRIBUTED;
            };
        }

        return switch (type) {
            case UNIFORM -> RANDOM_SUPPORT_UNIFORM_CONCURRENT;

            case MEAN_DISTRIBUTED -> RANDOM_SUPPORT_MEAN_DISTRIBUTED_CONCURRENT;
        };
    }

    SequentialIdFactory createSequentialIdFactory(final String name, final SequentialIdFactory sequentialIdFactory) {
        if (!isEnabled()) {
            return new SequentialIdFactoryStrategy(name, sequentialIdFactory);
        }

        return new SequentialIdFactoryStrategySynchronized(name, sequentialIdFactory);
    }
}
