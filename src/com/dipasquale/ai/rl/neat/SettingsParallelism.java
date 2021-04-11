package com.dipasquale.ai.rl.neat;

import com.dipasquale.ai.rl.neat.context.Context;
import com.dipasquale.ai.rl.neat.context.ContextDefaultParallelismSupport;
import com.dipasquale.ai.rl.neat.context.ContextDefaultParallelismSupportMultiThread;
import com.dipasquale.ai.rl.neat.context.ContextDefaultParallelismSupportSingleThread;
import com.dipasquale.common.RandomSupportFloat;
import com.dipasquale.threading.event.loop.EventLoopIterable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public final class SettingsParallelism {
    private static final RandomSupportFloat RANDOM_SUPPORT_UNIFORM = RandomSupportFloat.create(false);
    private static final RandomSupportFloat RANDOM_SUPPORT_MEAN_DISTRIBUTED = RandomSupportFloat.createMeanDistribution(false);
    private static final RandomSupportFloat RANDOM_SUPPORT_UNIFORM_CONCURRENT = RandomSupportFloat.create(true);
    private static final RandomSupportFloat RANDOM_SUPPORT_MEAN_DISTRIBUTED_CONCURRENT = RandomSupportFloat.createMeanDistribution(true);
    @Builder.Default
    private final EventLoopIterable eventLoop = null;

    boolean isEnabled() {
        return eventLoop != null;
    }

    int getNumberOfThreads() {
        if (!isEnabled()) {
            return 1;
        }

        return eventLoop.getConcurrencyLevel();
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

    ContextDefaultParallelismSupport create() {
        if (!isEnabled()) {
            Context.ParallelismSupport parallelism = new ContextDefaultParallelismSupportSingleThread();

            return new ContextDefaultParallelismSupport(parallelism);
        }

        Context.ParallelismSupport parallelism = new ContextDefaultParallelismSupportMultiThread(eventLoop);

        return new ContextDefaultParallelismSupport(parallelism);
    }
}
